package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.util.Merger.merge;
import static lombok.AccessLevel.PRIVATE;

import de.atruvia.ase.samman.buli.util.Merger.Mergeable;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = PRIVATE)
public class ToreUndGegentore implements Mergeable<ToreUndGegentore> {

	public static final ToreUndGegentore NULL = new ToreUndGegentore(0, 0);

	public static ToreUndGegentore toreUndGegentore(int tore, int gegentore) {
		return new ToreUndGegentore(tore, gegentore);
	}

	@Override
	public ToreUndGegentore mergeWith(ToreUndGegentore other) {
		return toreUndGegentore(merge(tore, other.tore), merge(gegentore, other.gegentore));
	}

	int tore;
	int gegentore;

}