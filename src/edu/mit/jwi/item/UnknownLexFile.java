/********************************************************************************
 * MIT Java Wordnet Interface (JWI)
 * Copyright (c) 2007-2011 Massachusetts Institute of Technology
 *
 * This is the non-commercial version of JWI.  This version may *not* be used
 * for commercial purposes.
 * 
 * This program and the accompanying materials are made available by the MIT
 * Technology Licensing Office under the terms of the MIT Java Wordnet Interface 
 * Non-Commercial License.  The MIT Technology Licensing Office can be reached 
 * at 617-253-6966 for further inquiry.
 *******************************************************************************/

package edu.mit.jwi.item;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents 'unknown' lexical files. This class implements internal caching,
 * much like the {@link Integer} class. Clients should use the static
 * {@link #getUnknownLexicalFile(int)} method to retrieve instances of this
 * class.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.1.4
 */
public class UnknownLexFile extends LexFile {
	
	// cache for unknown lexical file objects.
	private static Map<Integer, UnknownLexFile> lexFileMap = new HashMap<Integer, UnknownLexFile>();

	/**
	 * Obtain instances of this class via the static
	 * {@link #getUnknownLexicalFile(int)} method. This constructor is marked
	 * protected so that the class may be sub-classed, but not directly
	 * instantiated.
	 * 
	 * @param num
	 *            the number of the lexcial file
	 * @since JWI 2.1.4
	 */
	protected UnknownLexFile(int num) {
		super(num, "Unknown", "Unknown Lexical File", null);
	}

	/**
	 * Allows retrieval of an unknown lexical file object given the number.
	 * 
	 * @return UnknownLexFile the unknown lexical file object corresponding to
	 *         the specified number
	 * @throws IllegalArgumentException
	 *             if the specified integer is not a valid lexical file number
	 * @since JWI 2.1.4
	 */
    public static UnknownLexFile getUnknownLexicalFile(int num) {
    	checkLexicalFileNumber(num);
    	UnknownLexFile result =  lexFileMap.get(num);
    	if(result == null){
    		result = new UnknownLexFile(num);
    		lexFileMap.put(num, result);
    	}
    	return result;
    }

}
