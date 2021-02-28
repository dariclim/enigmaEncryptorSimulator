package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Daric Lim
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new ArrayList<Integer>();
        for (int i = 0; i < notches.length(); i += 1) {
            _notches.add(permutation().wrap(
                    alphabet().toInt(
                            notches.charAt(i)) - getRing()));
        }
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        return _notches.contains(setting());
    }

    @Override
    void advance() {
        set(setting() + 1 + getRing());
    }

    /** List of all notches in rotor.*/
    private ArrayList<Integer> _notches;
}
