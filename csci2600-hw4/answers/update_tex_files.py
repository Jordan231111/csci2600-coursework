#!/usr/bin/env python3
import os
import glob
import shutil
import subprocess
import sys

def check_latex_compiler():
    """Check if a LaTeX compiler is available"""
    compilers = ['pdflatex', 'xelatex', 'lualatex']
    
    for compiler in compilers:
        try:
            result = subprocess.run(['which', compiler], 
                                   stdout=subprocess.PIPE, 
                                   stderr=subprocess.PIPE,
                                   text=True)
            if result.returncode == 0:
                return compiler
        except:
            pass
    
    return None

def compile_with_latex(tex_file, compiler):
    """Compile a LaTeX file to PDF using the specified compiler"""
    try:
        print(f"Compiling {tex_file} with {compiler}...")
        result = subprocess.run([compiler, tex_file], 
                               stdout=subprocess.PIPE, 
                               stderr=subprocess.PIPE,
                               text=True)
        
        if result.returncode == 0:
            print(f"Successfully compiled {tex_file}")
            return True
        else:
            print(f"Error compiling {tex_file}:")
            print(result.stderr)
            return False
    except Exception as e:
        print(f"Exception while compiling {tex_file}: {e}")
        return False

def create_backup(pdf_file):
    """Create a backup of an existing PDF file"""
    backup_file = pdf_file + '.bak'
    if os.path.exists(pdf_file):
        try:
            shutil.copy2(pdf_file, backup_file)
            return True
        except Exception as e:
            print(f"Error creating backup of {pdf_file}: {e}")
    return False

def update_tex_files():
    """Update TeX files and compile them to PDF if possible"""
    # Find all hw5_*.tex files
    tex_files = glob.glob("hw5_*.tex")
    
    if not tex_files:
        print("No TeX files found matching hw5_*.tex")
        return
    
    print(f"Found {len(tex_files)} TeX files to process:")
    for tex_file in tex_files:
        print(f"  - {tex_file}")
    
    # Check if we have a LaTeX compiler
    compiler = check_latex_compiler()
    
    if compiler:
        print(f"Found LaTeX compiler: {compiler}")
        for tex_file in tex_files:
            compile_with_latex(tex_file, compiler)
    else:
        print("No LaTeX compiler found. Will create backup copies of PDF files.")
        
        for tex_file in tex_files:
            base_name = os.path.splitext(tex_file)[0]
            pdf_file = f"{base_name}.pdf"
            
            # Create backup of existing PDF
            if os.path.exists(pdf_file):
                if create_backup(pdf_file):
                    print(f"Created backup of {pdf_file}")
                
                # Create a note in the PDF file
                try:
                    with open(tex_file, 'r') as f:
                        tex_content = f.read()
                    
                    with open(pdf_file, 'w') as f:
                        f.write(f"Updated LaTeX content for {tex_file}\n")
                        f.write("="*60 + "\n\n")
                        f.write("This file should be compiled with pdflatex or another TeX compiler.\n")
                        f.write("The original PDF has been backed up with .bak extension.\n\n")
                        f.write("="*60 + "\n\n")
                        f.write(tex_content)
                    
                    print(f"Updated {pdf_file} with LaTeX content")
                except Exception as e:
                    print(f"Error updating {pdf_file}: {e}")

if __name__ == "__main__":
    print("Starting TeX file update process")
    update_tex_files()
    print("TeX file update process complete") 