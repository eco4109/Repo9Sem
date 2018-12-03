/*Programa para escribir en "Disco"
Para poder ser usado por la computadora virtual
*/
import java.io.*;
import java.nio.*;
import java.util.*;

public class writeDisc{
	//1 MB de Ram 
	public static byte[] buffer = new byte[1048576]; 

	public static void main(String[] args){
		setBuffer();
		write("DSK1.dsk",13); //Se escribe en el disco desde la posicion 7 
		System.out.println("\nSe escribió en el disco correctamente ... \n");
	}

	public static void setBuffer(){
		buffer[0]=(byte)0x20; //Empieza una instruccion es la 32 (MUE_DATO_BUS)
		buffer[1]=(byte)0x01; //Mueve al RB
		buffer[2]=(byte)0x00;
		buffer[3]=(byte)0x00;
		buffer[4]=(byte)0x00;
		buffer[5]=(byte)0x01; //un 1

		buffer[6]=(byte)0x20; //Empieza otranstruccione
		buffer[7]=(byte)0x02; //Mueve a RC
		buffer[8]=(byte)0x00;
		buffer[9]=(byte)0x00;
		buffer[10]=(byte)0x00;
		buffer[11]=(byte)0x02; //un 2

		buffer[12]=(byte)0x40; //MUE_REG_BUS
		buffer[13]=(byte)0x10; // de REGB a MMU_B1

		buffer[14]=(byte)0x40; //MUE_REG_BUS
		buffer[15]=(byte)0x21; // de REGC a 

		buffer[16]=(byte)0x42; //ALU_SUM
		buffer[17]=(byte)0x00; //

		buffer[18]=(byte)0x01; //MUE_BUS_REG
		buffer[19]=(byte)0x20; //de }l bus MM_B3 a RA


buffer[20]=(byte)0x00;
buffer[21]=(byte)0x00;
buffer[22]=(byte)0x00;
buffer[23]=(byte)0x00;
buffer[24]=(byte)0x00;
buffer[25]=(byte)0x00;
buffer[26]=(byte)0x00;
buffer[27]=(byte)0x00;
buffer[28]=(byte)0x00;
buffer[29]=(byte)0x00;
buffer[30]=(byte)0x00;
buffer[31]=(byte)0x00;
buffer[32]=(byte)0x00;
buffer[33]=(byte)0x00;
buffer[34]=(byte)0x00;
buffer[35]=(byte)0x00;
buffer[36]=(byte)0x00;
buffer[37]=(byte)0x00;
buffer[38]=(byte)0x00;
buffer[39]=(byte)0x00;
buffer[40]=(byte)0x00;
buffer[41]=(byte)0x00;
buffer[42]=(byte)0x00;
buffer[43]=(byte)0x00;
buffer[44]=(byte)0x00;
buffer[45]=(byte)0x00;
buffer[46]=(byte)0x00;
buffer[47]=(byte)0x00;
buffer[48]=(byte)0x00;
buffer[49]=(byte)0x00;
buffer[50]=(byte)0x00;
buffer[51]=(byte)0x00;
buffer[52]=(byte)0x00;
buffer[53]=(byte)0x00;
buffer[54]=(byte)0x00;
buffer[55]=(byte)0x00;
buffer[56]=(byte)0x00;
buffer[57]=(byte)0x00;
buffer[58]=(byte)0x00;
buffer[59]=(byte)0x00;
buffer[60]=(byte)0x00;
buffer[61]=(byte)0x00;
buffer[62]=(byte)0x00;
buffer[63]=(byte)0x00;
buffer[64]=(byte)0x00;
buffer[65]=(byte)0x00;
buffer[66]=(byte)0x00;
buffer[67]=(byte)0x00;
buffer[68]=(byte)0x00;
buffer[69]=(byte)0x00;
buffer[70]=(byte)0x00;
buffer[71]=(byte)0x00;
buffer[72]=(byte)0x00;
buffer[73]=(byte)0x00;
buffer[74]=(byte)0x00;
buffer[75]=(byte)0x00;



	}

	public static void write(String Archivo, int pos){
		try{
			RandomAccessFile binfile = new RandomAccessFile(Archivo, "rw");
			binfile.seek(pos);
			binfile.write(buffer);
			binfile.close();

		} catch(IOException ex){
			ex.printStackTrace();
			System.out.println("\n\7 Fatal error");
			System.exit(0);
			}
		}
}
