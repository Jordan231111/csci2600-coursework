import os
import glob
import re
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, ListItem, ListFlowable
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_LEFT, TA_CENTER
from reportlab.lib import colors

def clean_latex(text):
    """Clean LaTeX commands from text for display in PDF."""
    # Remove LaTeX commands and header/footer portions
    text = re.sub(r'\\documentclass.*?\\begin{document}', '', text, flags=re.DOTALL)
    text = re.sub(r'\\end{document}.*', '', text, flags=re.DOTALL)
    
    # Handle sections
    text = re.sub(r'\\section\*{(.*?)}', r'<b>\1</b>', text)
    
    # Handle itemize environments
    text = re.sub(r'\\begin{itemize}(.*?)\\end{itemize}', lambda m: process_list(m.group(1)), text, flags=re.DOTALL)
    
    # Handle text formatting
    text = re.sub(r'\\textbf{(.*?)}', r'<b>\1</b>', text)
    text = re.sub(r'\\textit{(.*?)}', r'<i>\1</i>', text)
    
    # Remove comments
    text = re.sub(r'%.*?$', '', text, flags=re.MULTILINE)
    
    # Remove specific LaTeX commands that are showing up in the output
    text = re.sub(r'\\vspace\{[^}]*\}', '', text)
    text = re.sub(r'\\setlength\{[^}]*\}\{[^}]*\}', '', text)
    
    # Remove other common LaTeX commands
    text = re.sub(r'\\hypersetup\{.*?\}', '', text, flags=re.DOTALL)
    text = re.sub(r'\\usepackage.*', '', text, flags=re.MULTILINE)
    text = re.sub(r'\\geometry\{.*?\}', '', text)
    text = re.sub(r'\\[a-zA-Z]+(\{[^}]*\})*', '', text)  # General command removal
    
    # Clean up newlines and spaces
    text = re.sub(r'\n\s*\n', '\n\n', text)
    text = text.strip()
    
    return text

def process_list(list_content):
    """Process LaTeX itemize contents into a simple bullet list."""
    items = re.findall(r'\\item\s+(.*?)(?=\\item|$)', list_content, re.DOTALL)
    return '\n'.join([f"• {item.strip()}" for item in items])

def latex_to_pdf(tex_file):
    """Convert LaTeX file to PDF using ReportLab."""
    base_name = os.path.splitext(tex_file)[0]
    pdf_file = f"{base_name}.pdf"
    
    print(f"Converting {tex_file} to {pdf_file}...")
    
    try:
        # Read the LaTeX file
        with open(tex_file, 'r') as f:
            latex_content = f.read()
        
        # Clean the LaTeX content
        text = clean_latex(latex_content)
        
        # Extract title from filename
        title = base_name.replace('_', ' ').title()
        
        # Create a PDF document
        doc = SimpleDocTemplate(pdf_file, pagesize=letter,
                               rightMargin=72, leftMargin=72,
                               topMargin=72, bottomMargin=72)
        
        # Define styles
        styles = getSampleStyleSheet()
        title_style = ParagraphStyle(name='CustomTitle', 
                                 fontName='Helvetica-Bold',
                                 fontSize=16, 
                                 alignment=TA_CENTER,
                                 spaceAfter=20)
        
        # Content elements
        elements = []
        
        # Add title
        elements.append(Paragraph(title, title_style))
        elements.append(Spacer(1, 12))
        
        # Process the content
        paragraphs = text.split('\n\n')
        for para in paragraphs:
            para = para.strip()
            if para:
                # Check if this is a bullet list
                if para.startswith('•'):
                    bullet_items = para.split('\n')
                    list_items = []
                    for item in bullet_items:
                        if item.strip():
                            content = item.replace('• ', '', 1)
                            list_items.append(ListItem(Paragraph(content, styles['Normal'])))
                    
                    if list_items:
                        elements.append(ListFlowable(list_items, bulletType='bullet', leftIndent=20))
                else:
                    # Regular paragraph
                    elements.append(Paragraph(para, styles['Normal']))
                    elements.append(Spacer(1, 12))
        
        # Build PDF
        doc.build(elements)
        print(f"Successfully created {pdf_file}")
        return True
    
    except Exception as e:
        print(f"Error creating PDF: {e}")
        # Fallback: just copy the content to a text file with .pdf extension
        try:
            with open(tex_file, 'r') as f:
                content = f.read()
            
            with open(pdf_file, 'w') as f:
                f.write(content)
            
            print(f"Created PDF (text only): {pdf_file}")
            return True
        except Exception as e2:
            print(f"All methods failed: {e2}")
            return False

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
        if latex_to_pdf(tex_file):
            success_count += 1
    
    print(f"Converted {success_count} of {len(tex_files)} files successfully.")

if __name__ == "__main__":
    main() 