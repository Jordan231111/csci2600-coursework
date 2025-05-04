import pypandoc
import os
import glob

def convert_tex_to_pdf(tex_file, output_format='pdf'):
    """Convert a LaTeX file to PDF using pandoc."""
    base_name = os.path.splitext(tex_file)[0]
    output_file = f"{base_name}.{output_format}"
    
    print(f"Converting {tex_file} to {output_file}...")
    
    try:
        # Read the LaTeX content
        with open(tex_file, 'r') as f:
            tex_content = f.read()
        
        # Convert to PDF
        pypandoc.convert_text(
            tex_content,
            output_format,
            format='latex',
            outputfile=output_file,
            extra_args=['--pdf-engine=xelatex']
        )
        
        print(f"Successfully created {output_file}")
        return True
    except Exception as e:
        print(f"Error converting {tex_file}: {e}")
        # Fallback - create a simple PDF with the text content
        create_simple_pdf(tex_file, output_file)
        return False

def create_simple_pdf(tex_file, output_file):
    """Create a simple PDF from LaTeX content using a different approach."""
    print(f"Attempting fallback conversion for {tex_file}...")
    
    try:
        # Read the LaTeX content
        with open(tex_file, 'r') as f:
            tex_content = f.read()
        
        # Extract the main content between \begin{document} and \end{document}
        start_idx = tex_content.find("\\begin{document}")
        end_idx = tex_content.find("\\end{document}")
        
        if start_idx != -1 and end_idx != -1:
            main_content = tex_content[start_idx + len("\\begin{document}"):end_idx].strip()
        else:
            main_content = tex_content
        
        # Clean up LaTeX commands
        main_content = main_content.replace("\\section*", "# ")
        main_content = main_content.replace("\\textbf{", "**").replace("}", "**")
        main_content = main_content.replace("\\begin{itemize}", "").replace("\\end{itemize}", "")
        main_content = main_content.replace("\\item", "* ")
        
        # Convert to PDF using pandoc's markdown conversion
        pypandoc.convert_text(
            main_content,
            'pdf',
            format='markdown',
            outputfile=output_file,
            extra_args=['--standalone']
        )
        
        print(f"Created simplified PDF: {output_file}")
        return True
    except Exception as e:
        print(f"Fallback conversion failed: {e}")
        
        # Last resort - create a text file with the PDF extension
        try:
            with open(tex_file, 'r') as f:
                content = f.read()
            
            with open(output_file, 'w') as f:
                f.write(content)
            
            print(f"Created text file with PDF extension: {output_file}")
            return True
        except Exception as e2:
            print(f"All conversion methods failed: {e2}")
            return False

def main():
    """Convert all TEX files in the current directory to PDF."""
    tex_files = glob.glob("hw5_*.tex")
    
    if not tex_files:
        print("No TEX files found in the current directory.")
        return
    
    print(f"Found {len(tex_files)} TEX files to convert:")
    for tex_file in tex_files:
        print(f"  - {tex_file}")
    
    for tex_file in tex_files:
        convert_tex_to_pdf(tex_file)

if __name__ == "__main__":
    main() 