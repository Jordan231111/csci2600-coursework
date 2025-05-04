import os
import re
import glob
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet

def extract_text_from_latex(filepath):
    """Extract clean text content from a LaTeX file."""
    with open(filepath, 'r') as file:
        content = file.read()
    
    # Extract content between \begin{document} and \end{document}
    pattern = r'\\begin{document}(.*?)\\end{document}'
    match = re.search(pattern, content, re.DOTALL)
    
    if not match:
        return None
    
    body = match.group(1)
    
    # Process section headings
    body = re.sub(r'\\section\*{(.*?)}', r'<h1>\1</h1>', body)
    
    # Process bold text
    body = re.sub(r'\\textbf{(.*?)}', r'<b>\1</b>', body)
    
    # Process lists
    body = re.sub(r'\\begin{itemize}(.*?)\\end{itemize}', process_list, body, flags=re.DOTALL)
    
    # Remove comments
    body = re.sub(r'%.*?$', '', body, flags=re.MULTILINE)
    
    # Remove all other LaTeX commands
    body = re.sub(r'\\[a-zA-Z]+(\[[^\]]*\])?(\{[^}]*\})*', '', body)
    
    # Clean up spacing
    body = re.sub(r'\n\s*\n', '\n\n', body)
    
    return body.strip()

def process_list(match):
    """Process a LaTeX itemize environment into a simple bullet list."""
    list_text = match.group(1) if isinstance(match, re.Match) else match
    
    # Extract individual items
    items = re.findall(r'\\item\s+(.*?)(?=\\item|$)', list_text, re.DOTALL)
    
    # Format as HTML-style list for reportlab
    result = "<ul>"
    for item in items:
        item = item.strip()
        # Remove any remaining LaTeX commands
        item = re.sub(r'\\[a-zA-Z]+(\{[^}]*\})*', '', item)
        result += f"<li>{item}</li>"
    result += "</ul>"
    
    return result

def create_pdf(text_content, output_path, title):
    """Create a PDF from extracted text content."""
    doc = SimpleDocTemplate(output_path, pagesize=letter,
                          rightMargin=72, leftMargin=72,
                          topMargin=72, bottomMargin=72)
    
    styles = getSampleStyleSheet()
    
    # Create a custom title style
    title_style = styles["Title"]
    
    # Create elements
    elements = []
    
    # Add title
    elements.append(Paragraph(title, title_style))
    elements.append(Spacer(1, 12))
    
    # Split content by paragraphs and headers
    paragraphs = re.split(r'(<h1>.*?</h1>|<ul>.*?</ul>)', text_content, flags=re.DOTALL)
    
    for para in paragraphs:
        para = para.strip()
        if not para:
            continue
            
        # Headers
        if para.startswith('<h1>') and para.endswith('</h1>'):
            heading = para[4:-5]  # Remove <h1> and </h1> tags
            elements.append(Spacer(1, 12))
            elements.append(Paragraph(heading, styles['Heading1']))
            elements.append(Spacer(1, 6))
        
        # Lists
        elif para.startswith('<ul>') and para.endswith('</ul>'):
            list_items = re.findall(r'<li>(.*?)</li>', para, flags=re.DOTALL)
            for item in list_items:
                bullet_text = "• " + item.strip()
                elements.append(Paragraph(bullet_text, styles['Normal']))
                elements.append(Spacer(1, 6))
        
        # Regular paragraphs
        else:
            elements.append(Paragraph(para, styles['Normal']))
            elements.append(Spacer(1, 12))
    
    # Build the PDF
    doc.build(elements)

def main():
    """Process all LaTeX files in the current directory."""
    tex_files = glob.glob("hw5_*.tex")
    
    if not tex_files:
        print("No TEX files found in the current directory.")
        return
    
    print(f"Found {len(tex_files)} TEX files to convert:")
    for tex_file in tex_files:
        print(f"  - {tex_file}")
    
    success_count = 0
    for tex_file in tex_files:
        base_name = os.path.splitext(tex_file)[0]
        pdf_file = f"{base_name}.pdf"
        title = base_name.replace('_', ' ').title()
        
        print(f"Converting {tex_file} to {pdf_file}...")
        
        # Extract content
        content = extract_text_from_latex(tex_file)
        
        if content:
            try:
                create_pdf(content, pdf_file, title)
                print(f"Successfully created {pdf_file}")
                success_count += 1
            except Exception as e:
                print(f"Error creating PDF: {e}")
                # Fallback - if PDF creation fails, create a simple text file
                with open(pdf_file, 'w') as f:
                    f.write(content)
                print(f"Created text-only file: {pdf_file}")
        else:
            print(f"Failed to extract content from {tex_file}")
    
    print(f"Converted {success_count} of {len(tex_files)} files successfully.")

if __name__ == "__main__":
    main() 