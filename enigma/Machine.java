package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Daric Lim
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;

        if (numRotors <= 1) {
            throw error("Not enough rotor slots");
        }
        if (pawls < 0 || pawls >= numRotors) {
            throw error("Incorrect amount of pawls");
        }
        if (allRotors.size() < numRotors) {
            throw error("More rotor sl ots than available rotors");
        }
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors;
        _currRotors = new HashMap<Integer, Rotor>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length == 0) {
            throw error("No rotors given");
        }
        if (rotors.length != numRotors()) {
            throw error("Invalid number of rotors");
        }
        HashMap<Integer, Rotor> newCurrRotors = new HashMap<>();
        int numMovingRotors = 0;
        for (int i = 0; i < rotors.length; i += 1) {
            boolean rotorFound = false;
            for (Rotor rotor : _allRotors) {
                if (rotors[i].equals(rotor.name())) {
                    if (i == 0 && !rotor.reflecting()) {
                        throw error("First rotor must reflect");
                    }
                    if (newCurrRotors.containsValue(rotor)) {
                        throw error("Rotor already in use");
                    }
                    if (rotor.rotates()) {
                        numMovingRotors += 1;
                    }
                    newCurrRotors.put(i + 1, rotor);
                    rotorFound = true;
                    break;
                }
            }
            if (!rotorFound) {
                throw error("Rotor not an option in _allRotors");
            }
        }
        if (numMovingRotors != numPawls()) {
            throw error("Invalid number of moving rotors");
        }
        _currRotors = newCurrRotors;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw error("Incorrect number of settings given");
        }
        for (int i = 0; i < setting.length(); i += 1) {
            char currChar = setting.charAt(i);
            if (!_alphabet.contains(currChar)) {
                throw error("Setting is not a char in alphabet");
            }
            _currRotors.get(i + 2).set(currChar);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        for (int i = 0; i < _alphabet.size(); i += 1) {
            if (_alphabet.toChar(i) != plugboard.alphabet().toChar(i)) {
                throw error("Invalid argument from plugboard");
            }
        }
        _plugboard = plugboard;
    }

    /** Set the RINGS of the Rotors. */
    void setRings(String rings) {
        if (rings.length() != numRotors() - 1) {
            throw error("Incorrect number of rings given");
        }
        for (int i = 0; i < rings.length(); i += 1) {
            char currChar = rings.charAt(i);
            if (!_alphabet.contains(currChar)) {
                throw error("Ring setting is not a char in alphabet");
            }
            _currRotors.get(i + 2).setRing(_alphabet.toInt(currChar));
        }
    }

    /** Resets the rings of the Rotors used. */
    void resetRings() {
        for (Rotor rotor: _allRotors) {
            rotor.setRing(0);
        }
    }


    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        Rotor firstRotor = _currRotors.get(_numRotors);
        boolean rightAtNotch = firstRotor.atNotch();
        firstRotor.advance();
        for (int i = _numRotors - 1; i > 1; i -= 1) {
            Rotor curr = _currRotors.get(i);
            boolean currAtNotch = curr.atNotch();
            if (curr.rotates() && rightAtNotch) {
                curr.advance();
                if (i + 1 < _numRotors) {
                    _currRotors.get(i + 1).advance();
                }
            }
            rightAtNotch = currAtNotch;
        }

        c = _alphabet.toInt(_plugboard.permute(_alphabet.toChar(c)));
        for (int i = _numRotors; i >= 1; i -= 1) {
            Rotor curr = _currRotors.get(i);
            c = curr.convertForward(c);
        }
        for (int i = 1; i <= _numRotors; i += 1) {
            Rotor curr = _currRotors.get(i);
            if (curr.reflecting()) {
                continue;
            }
            c = curr.convertBackward(c);
        }
        return _alphabet.toInt(_plugboard.permute(_alphabet.toChar(c)));
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            char currChar = msg.charAt(i);
            if (!_alphabet.contains(currChar)) {
                throw error("Contains characters not in alphabet");
            }
            result += _alphabet.toChar(convert(_alphabet.toInt(currChar)));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotor slots.*/
    private int _numRotors;

    /** Number of Pawls in Rotors.*/
    private int _numPawls;

    /** Permutation representing the plugboard.*/
    private Permutation _plugboard;

    /** Collection of all available Rotors.*/
    private Collection<Rotor> _allRotors;

    /** HashMap of all current Rotors in slots.*/
    private HashMap<Integer, Rotor> _currRotors;
}
