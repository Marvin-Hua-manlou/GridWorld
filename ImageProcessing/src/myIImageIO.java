
import java.awt.Graphics2D;  
import java.awt.Image;  
import java.awt.Toolkit;  
import java.awt.image.BufferedImage;  
import java.awt.image.ImageProducer;  
import java.awt.image.MemoryImageSource;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
      
import java.io.IOException;

    import javax.imageio.ImageIO;  
      

import javax.imageio.stream.FileImageInputStream;

import imagereader.IImageIO;  
      
    public class myIImageIO implements IImageIO{  
     // Image img;  
        
        public Image myRead(String filePath){
            File file = new File(filePath);
    	    try {
    		    FileImageInputStream infile = new FileImageInputStream(file);
    		    byte headInfo[] = new byte[54];
    		    infile.read(headInfo, 0, 54);
    		    int widthOfImage = (int)((headInfo[21] & 0xff)<<24 | (headInfo[20] & 0xff)<<16  | 
    		    		                  (headInfo[19] & 0xff)<<8 |(headInfo[18] & 0xff));
    		    int heightOfImage = (int)((headInfo[25] & 0xff)<<24 | (headInfo[24] & 0xff)<<16  | 
    		                              (headInfo[23] & 0xff)<<8 |(headInfo[22] & 0xff));
    		    int sizeOfImage = (int)((headInfo[37] & 0xff)<<24 | (headInfo[36] & 0xff)<<16  | 
    		                            (headInfo[35] & 0xff)<<8 |(headInfo[34] & 0xff));
    		    int sizeOfEmpty = sizeOfImage/heightOfImage - 3*widthOfImage;
    		    if (sizeOfEmpty == 4)
    		    	sizeOfEmpty = 0;
    		    
    		    //from down to top and from left to right
    		    byte allBytes[] = new byte[sizeOfImage];
    		    int arrayOfPix[] = new int[heightOfImage*widthOfImage];
    		    infile.read(allBytes, 0, sizeOfImage);
    		    int current = 0;
    		    for (int i = heightOfImage-1; i >= 0; i--) {
    		    	for (int j = 0; j < widthOfImage; j++) {
    		    		arrayOfPix[i*widthOfImage+j] = (int)((0xff)<<24 | (allBytes[current+2] & 0xff)<<16  | 
    		                                    (allBytes[current+1] & 0xff)<<8 | (allBytes[current] & 0xff));
    		    		current = current +3;
    		    	}
    		    	current = current + sizeOfEmpty;
    		    }
    		    infile.close();
    		    Image image = Toolkit.getDefaultToolkit().createImage((ImageProducer) new MemoryImageSource(widthOfImage, heightOfImage, arrayOfPix, 0, widthOfImage));
    		    return image;
    	    } 
    	    catch (IOException e) {
    		    e.printStackTrace();  
    	    }
            return (Image)null;
            //*/
          /* File file = new File(filePath);  
            try{  
               //创建FileInputStream类对象</span>  
                FileInputStream fis = new FileInputStream(file);  
                  
                //创建byte对象，分别存储位图头及位图信息</span>  
                byte bmpHead[] = new byte[14];  
                byte bmpInfo[] = new byte[40];  
                  
                fis.read(bmpHead, 0, 14);  
                fis.read(bmpInfo, 0, 40);  
                  
                //读取位图头#2-5字节，保存位图文件大小
                int sizeOfbmpHead = (int)( (bmpHead[5] & 0xff) << 24 | (bmpHead[4] & 0xff) << 16  
                        | (bmpHead[3] & 0xff) << 8 | (bmpHead[2] & 0xff) );  
                
                //读取位图信息，保存位图大小，每一个像素为3个字节
                int sizeOfbmpInfo = (int)( (bmpInfo[23] & 0xff) << 24 | (bmpInfo[22] & 0xff) << 16  
                        | (bmpInfo[21] & 0xff) << 8 | (bmpInfo[20] & 0xff) );  
                  
                //读取位图宽度,单位为像素
                int widthOfbmpInfo = (int)( (bmpInfo[7] & 0xff) << 24 | (bmpInfo[6] & 0xff) << 16  
                        | (bmpInfo[5] & 0xff) << 8 | (bmpInfo[4] & 0xff) );  
                  
                //读取位图高度，单位为像素
                int heightOfbmpInfo = (int)( (bmpInfo[11] & 0xff) << 24 | (bmpInfo[10] & 0xff) << 16  
                        | (bmpInfo[9] & 0xff) << 8 | (bmpInfo[8] & 0xff) );  
              
               //读取位图信息#28-29位，即bmpInfo#14-15位，判断位图是否为24位
                int byteNumOfbmp = (int)( (bmpInfo[15] & 0xff) << 8 | (bmpInfo[14] & 0xff) );  
                  
                if (byteNumOfbmp == 24){  
                      
                    //由于像素使用的字节若不是4的倍数，则会自动扩大，由此产生空白。因此我们需要在一开始计算出空白的大小 
                    int numOfEmptyByte = sizeOfbmpInfo / heightOfbmpInfo - 3*widthOfbmpInfo;  
                      
                    if(numOfEmptyByte == 4) {  
                        numOfEmptyByte = 0;  
                    }  
                   //定义数组从左到右，从下到上存储每个像素
                    int temp = 0;  
                      
                    int pixelArray[] = new int [widthOfbmpInfo * heightOfbmpInfo];  
                    byte bmpTotalByte[] = new byte[sizeOfbmpInfo];  
                    fis.read(bmpTotalByte, 0, sizeOfbmpInfo);  
                      
                    for(int i = heightOfbmpInfo-1; i >= 0; i-- ){  
                          
                        for( int j = 0; j < widthOfbmpInfo; j++ ){  
                            //第一个0xff << 24表示透明度
                            pixelArray[ widthOfbmpInfo * i + j ] = 0xff << 24  
                                    | (bmpTotalByte[temp+2] & 0xff) << 16   
                                    | (bmpTotalByte[temp+1] & 0xff) << 8   
                                    | (bmpTotalByte[temp] & 0xff) ;  
                              
                            temp += 3;  
                        }  
                        temp += numOfEmptyByte;  
                    }  
                      
                    img = Toolkit.getDefaultToolkit().createImage((ImageProducer) new MemoryImageSource(  
                            widthOfbmpInfo, heightOfbmpInfo, pixelArray, 0, widthOfbmpInfo));  
                }  
                fis.close();  
                return img;  
              
                  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
            return (Image) null;  
            */
        }  
       //根据二进制数据创建Image时可以使用API  

		@Override
		public Image myWrite (Image img, String filepath)
		{
			try{
				File imgFile = new File(filepath + "bmp");
				BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
				Graphics2D graph = buffer.createGraphics(); 
				graph.drawImage(img, 0, 0, null);
				graph.dispose();
				ImageIO.write(buffer, "bmp", imgFile );
				return img;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return img;
		} 
}