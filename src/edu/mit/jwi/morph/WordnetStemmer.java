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

package edu.mit.jwi.morph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.POS;

/**
 * This stemmer adds functionality to the simple pattern-based stemmer
 * {@code SimpleStemmer} by checking to see if possible stems are actually
 * contained in Wordnet. If any stems are found, only these stems are returned.
 * If no prospective stems are found, the word is considered unknown, and the
 * result returned is the same as that of the {@code SimpleStemmer} class.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public class WordnetStemmer extends SimpleStemmer {

    private final IDictionary dict;

	/**
	 * Constructs a WordnetStemmer that, naturally, requires a Wordnet
	 * dictionary.
	 * 
	 * @param dict
	 *            the dictionary to use; may not be <code>null</code>
	 * @throws NullPointerException
	 *             if the specified dictionary is <code>null</code>
	 * @since JWI 1.0
	 */
    public WordnetStemmer(IDictionary dict) {
        if(dict == null)
        	throw new NullPointerException();
        this.dict = dict;
    }
    
    /** 
     * Returns the dictionary in use by the stemmer; will not return <code>null</code>
     *
     * @return the dictionary in use by this stemmer
     * @since JWI 2.2.0
     */
    public IDictionary getDictionary(){
    	return dict;
    }

    /* 
     * (non-Javadoc) 
     *
     * @see edu.mit.jwi.morph.SimpleStemmer#findStems(java.lang.String, edu.mit.jwi.item.POS)
     */
    public List<String> findStems(String word, POS pos) {
    	
        word = normalize(word);
    	
    	if(pos == null) 
    		return super.findStems(word, null);

        SortedSet<String> result = new TreeSet<String>();
        
        // first look for the word in the exception lists
        IExceptionEntry entry = dict.getExceptionEntry(word, pos);
        boolean isException = false;
        if (entry != null){
        	isException = true;
        	result.addAll(entry.getRootForms());
        }

        // then look and see if it's in Wordnet; if so, the form itself is a stem
        if (dict.getIndexWord(word, pos) != null) 
        	result.add(word);
        
        if(isException) 
        	return new ArrayList<String>(result);

        // go to the simple stemmer and check and see if any of those stems are in WordNet
        List<String> possibles = super.findStems(word, pos);
        
        // check each algorithmically obtained root to see if it's in WordNet
        for (String possible : possibles) {
            if(dict.getIndexWord(possible, pos) != null){
                if (result == null) 
                	result = new TreeSet<String>();
                result.add(possible);
            }
        }

        if(result.isEmpty()) 
        	if(possibles.isEmpty()){
        		return Collections.<String>emptyList();
        	} else{
        		return new ArrayList<String>(possibles);
        	}
        return new ArrayList<String>(result);
    }
    
}
