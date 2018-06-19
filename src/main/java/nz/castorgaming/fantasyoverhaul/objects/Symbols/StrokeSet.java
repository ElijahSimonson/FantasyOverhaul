package nz.castorgaming.fantasyoverhaul.objects.Symbols;

import java.nio.ByteBuffer;
import java.util.Hashtable;

import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;

public class StrokeSet {
	
	private final byte[] strokes;
	private final int level;
	
	public StrokeSet(int levelIn, byte... strokesIn) {
		strokes = strokesIn;
		level = levelIn;
	}
	
	public StrokeSet(byte... strokesIn) {
		this(1, strokesIn);
	}
	
	public void addTo(Hashtable<ByteBuffer, SymbolEffect> table, Hashtable<ByteBuffer, Integer> enhanced, SymbolEffect effect) {
		ByteBuffer bb = ByteBuffer.wrap(strokes);
		table.put(bb, effect);
		enhanced.put(bb, level);
	}
	
	public void setDefaultFor(SymbolEffect effect) {
		effect.setDefaultStrokes(strokes);
	}
	
	public static class Stroke{
		public static final byte UP = 0, DOWN = 1, LEFT = 3, RIGHT = 2, UP_LEFT = 6, UP_RIGHT = 4, DOWN_LEFT = 5, DOWN_RIGHT = 7;
		public static final int[] STROKE_TO_INDEX, INDEX_TO_STROKE;
		
		static {
			STROKE_TO_INDEX = new int [] {0, 4, 2, 6, 1, 5, 7, 3};
			INDEX_TO_STROKE = new int [] {0, 4, 2, 7, 1, 5, 3, 6};
		}
	}

}
