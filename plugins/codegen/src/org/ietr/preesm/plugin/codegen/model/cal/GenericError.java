/* 
BEGINCOPYRIGHT X,UC
	
	Copyright (c) 2007, Xilinx Inc.
	Copyright (c) 2003, The Regents of the University of California
	All rights reserved.
	
	Redistribution and use in source and binary forms, 
	with or without modification, are permitted provided 
	that the following conditions are met:
	- Redistributions of source code must retain the above 
	  copyright notice, this list of conditions and the 
	  following disclaimer.
	- Redistributions in binary form must reproduce the 
	  above copyright notice, this list of conditions and 
	  the following disclaimer in the documentation and/or 
	  other materials provided with the distribution.
	- Neither the names of the copyright holders nor the names 
	  of contributors may be used to endorse or promote 
	  products derived from this software without specific 
	  prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
	CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
	INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
	CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
	NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
	HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
	OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	
ENDCOPYRIGHT
*/

package org.ietr.preesm.plugin.codegen.model.cal;

/**
 * A class which represents the known information about a particular
 * error in some source file/resource.  Line number, column number and
 * reason/message are stored here for easy reporting to the user.
 */
public class GenericError
{
    private String reason = "unknown";
    private String context = "unknown";
    private int lineNum = -1;
    private int colNum = -1;
    
	public GenericError (String reason, String context, int line, int col)
    {
        this.reason = reason;
        this.context = context;
        this.lineNum = line;
        this.colNum = col;
	}

    public String getReason ()
    {
        return this.reason;
    }
    
    public int getLineNumber () 
    {
        return this.lineNum;
    }
    
    public int getColumnNumber () 
    {
        return this.colNum;
    }

    public String toString ()
    {
        String line = lineNum >= 0 ? ("Line: " + lineNum + " "):"";
        String column = colNum >= 0 ? ("Column: " + colNum + " "):"";
        
        return "Error: " + reason + " " + line + column + context;
    }
    
}
