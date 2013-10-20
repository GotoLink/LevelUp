package assets.levelup;

import java.util.Arrays;

public final class BlockPosition {
	private final int[] data;

	public BlockPosition(int... pos) {
		this.data = pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		return Arrays.equals(this.getData(), ((BlockPosition) obj).getData());
	}

	@Override
	public int hashCode() {
		return this.getData().hashCode();
	}

	public int[] getData() {
		return data;
	}
}
