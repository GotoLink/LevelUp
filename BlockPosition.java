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
		if(obj == this)
			return true;
		else if(obj instanceof BlockPosition)
			return Arrays.equals(this.data,((BlockPosition)obj).data);
		else
			return false;
	}
	@Override
	public int hashCode()
	{
		return this.data.hashCode();
	}
}
