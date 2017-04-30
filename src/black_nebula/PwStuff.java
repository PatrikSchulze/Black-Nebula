package black_nebula;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.utils.compression.Lzma;

public class PwStuff
{


//	public static void main(String[] args)
//	{
//		MaData key = new MaData("__.__#~");
//		
//		try{
//			FileOutputStream fs1 = new FileOutputStream("k.d");
//			ObjectOutputStream oStream = new ObjectOutputStream(fs1);
//			oStream.writeObject(key);
//			oStream.close();
//			fs1.close();
//	    
//			FileInputStream fs2 = new FileInputStream("k.d");
//			FileOutputStream fs3 = new FileOutputStream(".d");
//			Lzma.compress(fs2, fs3);
//			fs2.close();
//			fs3.close();
//			
//			
//			
//			
//			FileInputStream fs4 = new FileInputStream(".d");
//			FileOutputStream fs5 = new FileOutputStream("yo.d");
//			Lzma.decompress(fs4, fs5);
//			fs4.close();
//			fs5.close();
//			
//			
//			FileInputStream fs6 = new FileInputStream("yo.d");
//			
//			ObjectInputStream ois = new ObjectInputStream(fs6);
//			
//			MaData yauza = (MaData)ois.readObject();
//			ois.close();
//			fs6.close();
//			
//			System.out.println(yauza.data);
//			
//			
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		File f2 = new File("k.d");
//		if (!f2.delete())
//		{
//			System.err.println("Couldnt delete k.d");
//		}
//		
//	
//		File f = new File("yo.d");
//		f.delete();
//		
//		
//	}
	
	public static class MaData implements Serializable
	{
		String data;
		
		public MaData(String _in)
		{
			data = _in;
		}
	}

}
