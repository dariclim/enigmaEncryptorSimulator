package enigma;

import java.util.ArrayList;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Daric Lim
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<String>();
        boolean expectingCycleEnd = false;
        String currCycle = "";
        for (int i = 0; i < cycles.length(); i += 1) {
            char currChar = cycles.charAt(i);
            if (Character.isWhitespace(currChar) && !expectingCycleEnd) {
                continue;
            } else if (Character.isWhitespace(currChar) && expectingCycleEnd) {
                throw error("bad permutation spec");
            }
            if (currChar == '(') {
                if (expectingCycleEnd) {
                    throw error("Invalid cycle in permutation.");
                }
                expectingCycleEnd = true;
            } else if (currChar == ')') {
                if (!expectingCycleEnd) {
                    throw error("Invalid cycle in permutation.");
                }
                addCycle(currCycle);
                currCycle = "";
                expectingCycleEnd = false;
            } else {
                currCycle += currChar;
            }
        }
        for (int i = 0; i < size(); i += 1) {
            boolean charFound = false;
            char currChar = _alphabet.toChar(i);
            for (String cycle: _cycles) {
                if (cycle.indexOf(currChar) != -1) {
                    charFound = true;
                }
            }
            if (!charFound) {
                _cycles.add(Character.toString(currChar));
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i += 1) {
            _alphabet.toInt(cycle.charAt(i));
            for (String s: _cycles) {
                if (s.contains(Character.toString(cycle.charAt(i)))) {
                    throw error("Cycle has chars from previous cycles.");
                }
            }
        }
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char curr = _alphabet.toChar(wrap(p));
        int indexCurr = -1;
        for (String s: _cycles) {
            int indexChar = s.indexOf(curr);
            if (indexChar != -1) {
                int indexPermute = (indexChar + 1) % s.length();
                if (indexPermute < 0) {
                    indexPermute += size();
                }
                indexCurr = _alphabet.toInt(s.charAt(indexPermute));
            }
        }
        if (indexCurr == -1) {
            throw error("Somehow not found in permutation cycles.");
        }
        return indexCurr;
    }

    /** Return the result of applying the inverse of this permutation
     *  to C modulo the alphabet size. */
    int invert(int c) {
        char curr = _alphabet.toChar(wrap(c));
        int indexCurr = -1;
        for (String s: _cycles) {
            int indexChar = s.indexOf(curr);
            if (indexChar != -1) {
                int indexPermute = (indexChar - 1) % s.length();
                if (indexPermute < 0) {
                    indexPermute += s.length();
                }
                indexCurr = _alphabet.toInt(s.charAt(indexPermute));
            }
        }
        if (indexCurr == -1) {
            throw error("Somehow not found in permutation cycles.");
        }
        return indexCurr;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int currIndex = _alphabet.toInt(p);
        int permuteIndex = permute(currIndex);
        return _alphabet.toChar(permuteIndex);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int currIndex = _alphabet.toInt(c);
        int invertIndex = invert(currIndex);
        return _alphabet.toChar(invertIndex);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String s: _cycles) {
            if (s.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation.*/
    private ArrayList<String> _cycles;
}
