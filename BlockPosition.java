package assets.levelup;

import java.util.Arrays;

public class BlockPosition {

	public int[] data;

	public BlockPosition(int...pos)
	{
		this.data = pos;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(super.equals(obj))
			return true;
		else if(obj instanceof BlockPosition)
			return Arrays.equals(this.data,((BlockPosition)obj).data);
		return false;	
	}
}
