
/*
	Simulación de una computadora: CPU
	20-Agosto-2018
	Programa para simular el cpu de una computadora virtual
*/

import java.io.*;
import java.nio.*;
import java.util.*;


public class pc{
	static final int RA = 0;
	static final int RB = 1;
	static final int RC = 2;
	static final int IP = 3;
	static final int SP = 4;
	static final int HP = 5;
	static final int BP = 6;
	static final int IX = 7;
	static final int CS = 8;
	static final int SS = 9;
	static final int HS = 10;
	static final int DS = 11;
	static final int IPR = 12;
	static final int OI = 13;

	static final int ALU_B1 = 0;
	static final int ALU_B2 = 1;
	static final int ALU_B3 = 2;
	static final int ALU_SUM = 66;
	static final int ALU_RES = 67;
	static final int ALU_MUL = 68;
	static final int ALU_DIV = 69;
	static final int MMU_B1 = 3;
	static final int MMU_B2 = 4;
	static final int MMU_B3 = 5;
	static final int MEM_B1 = 6;
	static final int MEM_B2 = 7;

	static final int MUE_REG_REG = 0;
	static final int MUE_BUS_REG = 1;
	static final int MUE_REG_BUS = 64;
	static final int MUE_BUS_BUS = 65;
	static final int MUE_DATO_REG = 32;
	static final int MUE_DATO_BUS = 96;
	static final int DUMP=33;
	static final int NOP=5;
	static final int COMP=6;
	static final int SBAND = 34;
	
	static final int MMU_OPER=70;
    static final int MMU_BY_PASS=71;
    static final int MEM_ESC_B=2;
    static final int MEM_ESC_P=3;
    static final int MEM_ESC_DP=4;
    static final int MEM_LECT_B=72;
    static final int MEM_LECT_P=73;
    static final int MEM_LECT_DP=74;
	static final int BANDON=192;
	static final int BANDOFF=193;
	static final int GETINT=128;


	public static boolean CPUInt = false;
	public static boolean EnInterrupcion;
	public static boolean dormidoV[] = new boolean[10]; //Vector de dormido (los procesos)
	public static int n = 0; //Variable para el numero de cosas en la cola de interrupciones
	public static int max = 10; //Máximo 10 interrupciones en el vector de interrupciones
	public static int[] Int_cola = new int[max]; //Vector de interrupciones, tamaño 10 
	public static int e;
	public static int s;
	public static int x;


	//Dispositivos y arranque del BIOS
	public static String BOOT="0000";
	public static int[] d_TIPO=new int[256];
	public static String[] d_IDENT=new String[256];
	public static int[] d_SECTOR=new int[256];
	public static int[] d_PISTAS=new int[256];
	public static int[] d_SECxPISTA=new int[256];
	public static boolean[] d_FMTBN=new boolean[256];
	public static boolean[] d_FMTSO=new boolean[256];
	public static boolean[] d_BOOT=new boolean[256];
	public static int[] d_IDSO=new int[256];
	public static int[] d_POSB=new int[256];	//Pos Brazo
	public static int[] d_POSS=new int[256];	//Pos Sector
	public static int idx_BOOT=0;
	public static int idx_MAX=0;
	public static int disp_ES=0;
	

	static  byte buffer[] = new byte[128];
	//Registros del CPU
	public static int[] R = new int[14];
	//Buses de todo el sistema (compuertas)
	public static int[] B = new int[16];
	//PSW de la computadora (banderas)
	public static boolean[] PSW = new boolean[16];
	//Memoria principal RAM
	// int = 4 byte´s
	// 1Mb de RAM
	public static byte[] RAM = new byte[1048576]; 
	//Buffer Arrays contains BYTES
	public static byte[] r2b = new byte[2];
	public static byte[] r4b = new byte[4];
	//Buffer Arrays, contains bits in each position
	public static String[] r2binary = new String[2];
	public static String[] r4binary = new String[4];
	static byte buff_trad[]= new byte[2];
	static byte buff_dato[]= new byte[4];

	public static int[] data = new int[4];
	static int dato;

	//public static int dato[];
	static int cod_inst = 0;
	static int orig; 
	static int dest;
	static int temp1, temp2, temp3, temp4, temp5;
	static float res_alu;
	static String NoSirve;

	public static String pausa(){
		BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in)) ;
		String nada = null;
		try{
			nada = entrada.readLine();
		}catch(Exception e){
			System.err.println(e);
		}
		return nada;
	}
	public static int verificaValor(int valor, int num){
		int masc1=128;
		int masc2=64;
		int masc3=32;
		int R;
		
		if(num==1){
			R = valor & masc1;
			
			if(R==128){
				return 1;
			}else{
				return 0;
			}
		}else if(num==2){
			R = valor & masc2;
			if(R==64){
				return 1;
			}else{
				return 0;
			}
		}else if(num==3){
			R = valor & masc3;
			if(R==32){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
		
	}
	

	public static void printArrayinDec(byte[] array){ //Function for print an Array in decimal, a BYTE comes in
		for (int i=0;i<array.length;i++) {
			System.out.println(array[i]&0xFF);
		}
	}

	public static void printArray(String[] array){ //Function for print an Array, a STRING comes in
		for (int i=0;i<array.length;i++) {
			System.out.println(array[i]);
		}
	}

	public static String bytes_to_bits(byte stringBytes){ //Method for convert BYTES into bits String
		String bb = Integer.toBinaryString(stringBytes); //First the BYTE it´s convert into a String
		//we need to complete the Byte to 8-bits if it's less than 8
		if(bb.length()<8){
			while(bb.length()<8){
				bb = "0"+bb;
			}
		}
		String inBinary = bb.substring(bb.length()-8, bb.length());//Split the String , we need only the lasts 8 bits, so cut it
		return inBinary;
	}

	public static void capta(){
		int x=forceint(IEEE_a_flotante(R[BP]) + IEEE_a_flotante(R[IP]));
		buff_trad[0]=RAM[x];
		x++;
		buff_trad[1]=RAM[x];
		x++;
		buff_dato[0]=RAM[x];
		x++;
		buff_dato[1]=RAM[x];
		x++;
		buff_dato[2]=RAM[x];
		x++;
		buff_dato[3]=RAM[x];

		/*System.out.printf("Buffer de traduccion: %02X", buff_trad[0]);
		System.out.printf("Buffer de traduccion: %02X", buff_trad[1]);
		System.out.printf("Buffer de DATO: %02X", buff_dato[0]);
		System.out.printf("Buffer de DATO: %02X", buff_dato[1]);
		System.out.printf("Buffer de DATO: %02X", buff_dato[2]);
		System.out.printf("Buffer de DATO: %02X", buff_dato[3]);
		*/
	}

	public static int char_to_int(char caracter){ //Funcition for transform CHARACTERS INTO INTEGERS
		int aux = Integer.parseInt(Character.toString(caracter));
		return aux;
	}

	public static String[] float_to_IEEE(float numberF){ //Function for transform a FLOAT number into IEE
		//Like the "ieee.java teacher´s code" but in one function
		//System.out.println("Lo que entró (en flotante): "+ numberF);
		int numberI = Float.floatToIntBits(numberF);
		//System.out.println("Ya convertido entero: "+numberI);
		byte datos[] = new byte[4];
		String datos2[] = new String[4];
		String datos3[] = new String[4];
		datos[0] = (byte) (numberI>>> 24);
		datos[1] = (byte) (numberI>>> 16);
		datos[2] = (byte) (numberI>>> 8);
		datos[3] = (byte) (numberI>>> 0);
		//Transform into decimal

		datos2[0] =String.format("%02x", datos[0]&0xFF);
		datos2[1] = String.format("%02x", datos[1]&0xFF);
		datos2[2] = String.format("%02x", datos[2]&0xFF);
		datos2[3] = String.format("%02x", datos[3]&0xFF);


		return datos2;
	}

	public static String arrayUnion (String[] array){ //Function for join the elements of an array
		String result ="";
		for (int i=0;i<array.length;i++) {
			result = result+array[i];
		}
		return result.toUpperCase();
	}

	public static void traduce(){
		int k = verificaValor(forceint(buff_trad[0]),1);
		int s = verificaValor(forceint(buff_trad[0]),2);
		int L = verificaValor(forceint(buff_trad[0]),3);
		
		if(k==1 && (!PSW[15])){
			System.out.println("\7 Violacion de Acceso al Modo Kernel");
			System.exit(4);
		}
		cod_inst=buff_trad[0];

		int largo = 2+4*L;
		int aux;
		if(largo==6){
			dato = 0;
			aux = buff_dato[0];
			aux = aux << 24;
			dato = dato + aux;
			aux = buff_dato[1];
			aux =  (aux <<16)&0x00FF0000;
			dato = dato + aux;
			aux = buff_dato[2];
			aux =  aux << 8;
			dato = dato + aux;
			aux = (buff_dato[3])&0x000000FF;
			dato = dato + aux;
		}
		float j = IEEE_a_flotante(R[IP])+largo;
		R[IP] = flotante_a_IEEE(j);
		float f = IEEE_a_flotante(R[OI]);
		float w = s*(f+s*largo);
		R[OI]=flotante_a_IEEE(w);
		orig = (buff_trad[1]&0xF0)>>>4;
		dest = (buff_trad[1]&0xF);

		/*System.out.println("Codigo de la Instruccion = "+cod_inst);
		System.out.println("Origen = "+orig);
		System.out.println("Destino = "+dest);
		*/
}
	public static float IEEE_a_flotante( int f){
		float yy = Float.intBitsToFloat((f));
		return yy;
	}
	public static int flotante_a_IEEE(float f){
		int num = Float.floatToIntBits(f);
		return num;

	}
	public static int binary_to_int(String numero){
		int num;
		return num=Integer.parseInt(numero,2);
	}

	public static void ejecuta(){
		switch(cod_inst){
			case MUE_REG_REG:
				R[dest]=R[orig];
				break;
			case MUE_REG_BUS:
				if(dest==ALU_B3||dest==MMU_B3||dest==MEM_B2){
					System.out.println("\7 Corto circuito");
					System.exit(4);
				}
				B[dest]=R[orig];
				break;
			case MUE_BUS_REG:
				if(orig==MMU_B3){
					System.out.println("\7 Corto Circuito");
					System.exit(4);
				}
				R[dest]=B[orig];
				break;
			case MUE_BUS_BUS:
				if(dest==ALU_B3||dest==MMU_B3||dest==MEM_B2||orig==MMU_B3||orig==MEM_B1){
					System.out.println("\7 Corto Circuito");
					System.exit(4);
				}
				B[dest]=B[orig];
				break;
			case ALU_SUM:
				res_alu=IEEE_a_flotante(B[ALU_B1])+IEEE_a_flotante(B[ALU_B2]);
				B[ALU_B3]=flotante_a_IEEE(res_alu);
				break;
			case ALU_RES:
				res_alu=IEEE_a_flotante(B[ALU_B1])-IEEE_a_flotante(B[ALU_B2]);
				B[ALU_B3]=flotante_a_IEEE(res_alu);
				break;
			case ALU_MUL:
				res_alu=IEEE_a_flotante(B[ALU_B1])*IEEE_a_flotante(B[ALU_B2]);
				B[ALU_B3]=flotante_a_IEEE(res_alu);
				break;
			case ALU_DIV:
				res_alu=IEEE_a_flotante(B[ALU_B1])/IEEE_a_flotante(B[ALU_B2]);
				B[ALU_B3]=flotante_a_IEEE(res_alu);
				break;
			case MUE_DATO_REG:
				if (dest == IP) {
					R[dest]=flotante_a_IEEE(dato);
				}else{
					R[dest]=dato;
				}
				break;
			case DUMP:
				dump(dato);
				break;
			case MUE_DATO_BUS:
				if(dest==ALU_B3||dest==MMU_B3||dest==MEM_B2){
					System.out.println("\7Corto Circuito");
					System.exit(4);
				}
				B[dest]=dato;
				break;
			case COMP:
				for(int i=0;i<=14;i++){
					PSW[i]=false;
					float r1 = IEEE_a_flotante(R[orig]);
					float r2 = IEEE_a_flotante(R[dest]);
					if(r1==r2)
						PSW[0]=true;
					if(r1>r2)
						PSW[1]=true;
					if(r1<r2)
						PSW[2]=true;
					if(r1>=r2)
						PSW[3]=true;
					if(r1<=r2)
						PSW[4]=true;
					if(r1!=r2)
						PSW[5]=true;
				}
				break;         
            case SBAND:
                   if(PSW[orig]){
                       R[dest]=dato;
                   }
            case MMU_OPER:
                   res_alu=IEEE_a_flotante(B[MMU_B1])+IEEE_a_flotante(B[MMU_B2]);
                   if(forceint(res_alu)<=999&&!PSW[15]){
                       System.out.println("\7Violacion de memoria");
                       System.exit(4);
                   }else
                       B[MMU_B3]=flotante_a_IEEE(res_alu);
                   break;
            case MMU_BY_PASS:
                   temp1=forceint(IEEE_a_flotante(B[MMU_B2]));
                   if(temp1<=999&&!PSW[15]){
                       System.out.println("\7Violacion de memoria");
                       System.exit(4);
                   }else
                       B[MMU_B3]=B[MMU_B2];
                   break;
            case MEM_LECT_B:
            	B[MEM_B2] = RAM[forceint(IEEE_a_flotante(MMU_B3))] & 0xFF;
            	break;
            case MEM_LECT_P:
            	temp1 = forceint(IEEE_a_flotante(B[MMU_B3]));
            	buff_dato[0] = (byte)0x00;
            	buff_dato[1] = (byte)0x00;
            	buff_dato[2] = RAM[temp1++];
            	buff_dato[3] = RAM[temp1];
            	B[MEM_B2] = ByteBuffer.wrap(buff_dato).getInt();
            	break;
            case MEM_LECT_DP:
            	temp1 = forceint(IEEE_a_flotante(B[MMU_B3]));
            	buff_dato[0] = RAM[temp1++];
            	buff_dato[1] = RAM[temp1++];
            	buff_dato[2] = RAM[temp1++];
            	buff_dato[3] = RAM[temp1];
            	break;
            case MEM_ESC_B:
            	RAM[forceint(IEEE_a_flotante(B[MMU_B3]))] = (byte)(B[MEM_B1]>>>0);
            	break;
            case MEM_ESC_P:
		        temp2 = forceint(IEEE_a_flotante(B[MMU_B3]));
		        RAM[temp2++]= (byte)(B[MEM_B1]>>>0);
            	break;
            case MEM_ESC_DP:
            	temp2 = forceint(IEEE_a_flotante(B[MMU_B3]));
            	RAM[temp2++] = (byte)(B[MEM_B1]>>>24);
            	RAM[temp2++] = (byte)(B[MEM_B1]>>>16);
            	RAM[temp2++] = (byte)(B[MEM_B1]>>>8);
            	RAM[temp2] = (byte)(B[MEM_B1]>>>0);
            	break;
			case BANDON:
				PSW[orig] = true;
				break;
			case BANDOFF:
				PSW[orig] = false;
				break;
			default:
				break;
		}
		
	}
	public static int forceint( float f){
		int valorEntero = (int) f;
		return valorEntero;
	}
	
	public static void push(int x){
		if(((EnInterrupcion)&&(x==1))){ //Si estoy en interrupcion y entra una d ereloj, NO HACER NADA x = 0 <- interrupcion de reloj
			System.out.print(" ");
		}else{
			if (n==max){
				System.out.println("La cola está llena");
				System.exit(4);
			}else{
				n = n+1;
				Int_cola[e] = x;
				if(e==(max-1)){
					e = 0;
				}else{
					e = e+1;
				}
			}
		}
	}

	public static int pop(){
		if(n==0){
			System.out.println("cola vacia");
		}else{
			n = n-1;
			x = Int_cola[s];
			if(s==(max-1)){
				s=0;
			}else{
				s = s+1;
			}
		}
		return x;
	}
	
	public static void dump(int mem){
		String var1, var2;
		String[] NomReg=new String[14];
		String[] NomBus=new String[8];
		NomReg[0]="RA";
		NomReg[1]="RB";
		NomReg[2]="RC";
		NomReg[3]="IP";
		NomReg[4]="SP";
		NomReg[5]="HP";
		NomReg[6]="BP";
		NomReg[7]="IX";
		NomReg[8]="CS";
		NomReg[9]="SS";
		NomReg[10]="HS";
		NomReg[11]="DS";
		NomReg[12]="IPR";
		NomReg[13]="OI";
		NomBus[0]="ALU_B1";
		NomBus[1]="ALU_B2";
		NomBus[2]="ALU_B3";
		NomBus[3]="MMU_B1";
		NomBus[4]="MMU_B2";
		NomBus[5]="MMU_B3";
		NomBus[6]="MEM_B1";
		NomBus[7]="MEM_B2";
		System.out.println("\nPresione x para terminar el volcado\n\n");
		System.out.println("\n\tBANDERAS\t      REGISTROS\t\t    BUSES\n");
		for(int j=0;j<=15;j++){
			var1="";	var2="";
			if(j<=13)
				var1=NomReg[j]+"=["+String.format("%08X",R[j])+"]";
			if(j<=7)
				var2=NomBus[j]+"=["+String.format("%08X",B[j])+"]";
			System.out.printf("\t%-22s%-22s%-22s\n","PSW"+j+"=["+PSW[j]+"]",var1,var2);
		}
		System.out.print("\n - ");
		NoSirve=pausa();
		System.out.print("\n");
		if(!NoSirve.equals("x")){
			NoSirve="";
			temp1=forceint(IEEE_a_flotante(mem)); 
			temp1=temp1-(temp1%16);
			temp2=0;
			temp3=0;
			while(temp1<1048575&&(!NoSirve.equals("x"))){
				System.out.printf(" %07d : ",temp1+temp3*16);
				for(int i=0;i<=7;i++)
					System.out.printf("%02X ",RAM[temp1+i+temp3*16]);
				System.out.print("| ");
				for(int i=8;i<=15;i++)
					System.out.printf("%02X ",RAM[temp1+i+temp3*16]);
				for(int i=0;i<=15;i++){
					temp4=(RAM[temp1+i+temp3*16]&0xFF);
					if(temp4>=32&&temp4<=254)
						System.out.printf("%c",temp4);
					else
						System.out.print(".");
				}
				temp3++;
				System.out.print("\n");
				if(temp2++>=7){
					System.out.print("\n - ");
					NoSirve=pausa();
					System.out.print("\n");
					temp2=0;
				}
			}
		}
	}

	public static void main(String[] argumento) {
		if(argumento.length!=0){
			if(argumento[0].equals("BIOS"))
				BIOS.IniciaBIOS();
			if(argumento.length==2 && argumento[0].equals("CreaDD")){
				CrearDisco.creaHD(argumento[1]);
			}
			if(argumento.length==3 && argumento[0].equals("CreaUSB")){
				CrearUSB.creaUSB(argumento[1],argumento[2]);
			}
			System.exit(0);
		}

		//Procesos de la computadora virtual (Controlador de interrupciones, reloj y CPU)
		Computadora COMPAQ = new Computadora();
		reloj CLOCK = new reloj();
		c_interrup C_Interrup = new c_interrup();

		BIOS.VerifDispo();
		BIOS.StartUP();
		int NoCasiilasPintar = 50;
		System.out.printf("\nSe han cargado de disco a la RAM exitosamente, mostrando %d direcciones de memoria: \n\n",NoCasiilasPintar);
		for (int i=0; i<=NoCasiilasPintar ; i++) {
			//Pinta la RAM al empezar 
			 System.out.printf("RAM[%d] = %02X    ",i,RAM[i]);
			 if(((i%8) == 0)&&(i > 0)){
			 	System.out.println("\n");
			 }
		}
		System.out.println("\n");
		PSW[15] = true;
		for (int i = 0; i<=13 ; i++) {
		 	R[i] = 0;
		 } 
		//Pausa antes de empezar la computadora y el reloj
		for(int l =0; l< dormidoV.length; l++) { //Inicializar el vector de dormido
			dormidoV[l] = false; 
		}
		System.out.println("\nVector dormidoV inicializado\n ");
		System.out.println("Press Any Key To Continue...");
		new java.util.Scanner(System.in).nextLine();



		COMPAQ.start();
		C_Interrup.start();
		CLOCK.start();
	}
}

class Computadora extends Thread{
	public void run(){
		while(!pc.PSW[14]){
			int y;
			int x;
			int aux;
			int ipd;
			int bpd;
			int r0, r1, r2, r3;

			byte bait, bait2;
			int entero = 0; 

			pc.capta();
		 	pc.traduce();
		 	pc.ejecuta();
			System.out.println("Al terminar este Ciclo, el IP quedo como:     "+pc.forceint(pc.IEEE_a_flotante(pc.R[pc.IP])));
		 	System.out.println("Un Ciclo de Fetch Terminado ...");
		 	
		 	if(pc.CPUInt){
		 		System.out.println("--- Ciclo de Fetch Interrumpido --- \7");
				pc.CPUInt = false;
				pc.EnInterrupcion = true;
				//Respaldar el BP e IP
				//System.out.printf("\n\n(hexadecimal) RAM[0: %02X \n",pc.RAM[0]);
				//System.out.printf("\n\n(hexadecimal) RAM[1: %02X \n",pc.RAM[1]);

				r0 = pc.RAM[0]&0xFF;
				r1 = pc.RAM[1]&0xFF;

				//System.out.println("\n\n(decimal) RAM[0:  "+r0+" \n");
				//System.out.println("\n\n(decimal) RAM[1:  "+r1+" \n\n");

				System.out.println("El IP: "+ pc.forceint(pc.IEEE_a_flotante(pc.R[pc.IP])));
				System.out.println("El BP: "+ pc.forceint(pc.IEEE_a_flotante(pc.R[pc.BP])));

				y = (pc.RAM[0]&0xFF) + (pc.RAM[1]&0xFF);

				//System.out.println("Suma de la RAM [0] y RAM[1] : "+ y);

				pc.RAM[y] = (byte) (pc.forceint(pc.IEEE_a_flotante(pc.R[pc.BP])));
				pc.RAM[y+4] = (byte) (pc.forceint(pc.IEEE_a_flotante(pc.R[pc.IP])));

				//System.out.printf("Entonces, RAM en la posicion %d  =  %02X",y, pc.RAM[y]);
				//System.out.printf("Entonces, RAM en la posicion %d =  %02X",y+4,pc.RAM[y+4]);

				//System.out.printf("\n\n(hexadecimal) RAM[2: %02X \n",pc.RAM[2]);
				//System.out.printf("\n\n(hexadecimal) RAM[3: %02X \n",pc.RAM[3]);

				r2 = pc.RAM[2]&0xFF;
				r3 = pc.RAM[3]&0xFF;

				//System.out.println("\n\n(decimal) RAM[2:  "+r2+" \n");
				//System.out.println("\n\n(decimal) RAM[3:  "+r3+" \n\n");

				pc.R[pc.BP] = pc.flotante_a_IEEE((float) (r2+r3));

				System.out.println("BP QUEDA COMO (en IEEE): "+pc.R[pc.BP]);
				System.out.println("BP QUEDA COMO (en float): "+pc.IEEE_a_flotante(pc.R[pc.BP]));

				pc.R[pc.IP] = pc.flotante_a_IEEE(0);

				System.out.println("IP QUEDA COMO (en IEEE): "+pc.R[pc.IP]);
				System.out.println("IP QUEDA COMO (en float): "+pc.IEEE_a_flotante(pc.R[pc.IP]));
	
				//pc.RAM[y+4] = pc.BP;
				
				//forceint(IEEE_a_flotante(R[BP]) + IEEE_a_flotante(R[IP]))

				//pc.RAM[pc.BP] = pc.RAM[3] + pc.RAM[4]
				
				//REESPALDAR PROCEDIMIENTO ACTUAL!!!
		 		//pc.PSW[15] = true; //Cambiamos a modo kernel
		 	}	
		}
	}

}

/*
12/11/2018

red()
	push(1)
	
e_s()
	push(2)


VECTOR DE INTERRUPCIONES
1	8500	Interrupcion de reloj
2	2300	Interrupción de red
3	1000	Interrupción de E/S
4           Controlasor de Interrupciones
*/
class c_interrup extends Thread{
	public void run(){
		System.out.println("Se inicio el controlador de Interrupciones");
		while(true){
			while(pc.dormidoV[4]);
			while(pc.n == 0); //Mientras la cola de interrupciones estpa vacia, se cicla allí
			pc.CPUInt = true;
			pc.dormidoV[4] = true;
		}
	}
}

class reloj extends Thread{ //Proceso del reloj
	public void run(){ 
		long quantum = 1000; //Interrupción de justicia
		long horaSistema, horaInicial, diferencia;
		horaInicial = System.currentTimeMillis();
		while(true){
			horaSistema = System.currentTimeMillis();
			diferencia = horaSistema - horaInicial;	
			if((diferencia >= quantum - 20)&&(diferencia <= quantum +20)){
				System.out.println("--- INTERRUPCION DE RELOJ ---");
				pc.CPUInt = true;
				pc.push(1);
				horaInicial = horaSistema;					
			}
		}
	}

}
