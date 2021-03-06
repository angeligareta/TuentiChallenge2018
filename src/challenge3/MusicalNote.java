/**
 * Enumerator that represents the notes as double values and the sharp ones.
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 27-04-2018
 */
package challenge3;

public enum MusicalNote {
	A(0), 
	AS(0.5), 
	B(1), 
	C(1.5), 
	CS(2), 
	D(2.5), 
	DS(3), 
	E(3.5), 
	F(4), 
	FS(4.5), 
	G(5), 
	GS(5.5);

	/** Pitch of the note. */
	private double pitch;

	/**
	 * Default constructor.
	 * 
	 * @param pitch
	 */
	private MusicalNote(double pitch) {
		this.pitch = pitch;
	}

	/**
	 * Returns the note with the name passed by argument.
	 * 
	 * @param noteName
	 * @return
	 */
	private static MusicalNote getNoteWithName(String noteName) {
		for (MusicalNote note : MusicalNote.values()) {
			if (note.name().equals(noteName)) {
				return note;
			}
		}
		throw new IllegalArgumentException("Bad note: " + noteName);
	}

	/**
	 * Returns the note with the normalizing the passed by argument.
	 * 
	 * @param pitchInPosition
	 * @return
	 */
	public static MusicalNote getNoteWithPitch(double notePitch) {
		Double realPitch;
		if (notePitch < 0 && notePitch >= -6) {
			realPitch = 6 + notePitch;
		}
		else if (notePitch >= 6 && notePitch <= 12) {
			realPitch = notePitch % 6;
		}
		else {
			realPitch = notePitch;
		}

		for (MusicalNote note : MusicalNote.values()) {
			if (realPitch.equals(note.getPitch())) {
				return note;
			}
		}

		throw new IllegalArgumentException("Bad note pitch: " + notePitch);
	}

	/**
	 * Normalize a note, if it's a flat one it converts to a equivalent one.
	 * 
	 * @param string
	 * @return
	 */
	public static MusicalNote normalizeNote(String noteName) {
		if (noteName.length() > 2) {
			throw new IllegalArgumentException("Bad note: " + noteName);
		}

		String naturalNote = noteName.substring(0, 1);
		double pitch = getNoteWithName(naturalNote).getPitch();

		if (noteName.length() > 1) {
			String noteModification = noteName.substring(1, 2);
			if (noteModification.equals("#")) {
				pitch += 0.5;
			}
			else if (noteModification.equals("b")) {
				pitch -= 0.5;
			}
			else {
				throw new IllegalArgumentException("Bad note: " + noteName);
			}
		}

		return getNoteWithPitch(pitch);
	}

	/**
	 * Getter of the pitch
	 * 
	 * @return the pitch
	 */
	public double getPitch() {
		return pitch;
	}

	/*
	 * (non-Javadoc) Overloaded to string method to show correctly the sharp notes.
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		if (this.name().endsWith("S")) {
			return this.name().substring(0, 1) + "#";
		}
		else {
			return this.name();
		}
	}
}
