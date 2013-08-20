package assets.levelup;

import java.util.Arrays;

public class BlockPosition {

	public int[] position;

	public BlockPosition(int...pos)
	{
		this.position = pos;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(super.equals(obj))
			return true;
		else if(obj instanceof BlockPosition)
			return Arrays.equals(this.position,((BlockPosition)obj).position);
		return false;	
	}
}
