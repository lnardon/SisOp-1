// PUCRS - Escola Politécnica - Sistemas Operacionais
// Prof. Fernando Dotti
// Código fornecido como parte da solução do projeto de Sistemas Operacionais
//
// Fase 1 - máquina virtual (vide enunciado correspondente)
//

import java.util.*;

public class Sistema {
	
	// -------------------------------------------------------------------------------------------------------
	// --------------------- H A R D W A R E - definicoes de HW ---------------------------------------------- 

	// -------------------------------------------------------------------------------------------------------
	// --------------------- M E M O R I A -  definicoes de opcode e palavra de memoria ---------------------- 
	
	public class Word { 	// cada posicao da memoria tem uma instrucao (ou um dado)
		public Opcode opc; 	//
		public int r1; 		// indice do primeiro registrador da operacao (Rs ou Rd cfe opcode na tabela)
		public int r2; 		// indice do segundo registrador da operacao (Rc ou Rs cfe operacao)
		public int p; 		// parametro para instrucao (k ou A cfe operacao), ou o dado, se opcode = DADO

		public Word(Opcode _opc, int _r1, int _r2, int _p) {  
			opc = _opc;   r1 = _r1;    r2 = _r2;	p = _p;
		}
	}
    // -------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------
    // --------------------- C P U  -  definicoes da CPU ----------------------------------------------------- 

	public enum Opcode {
		DATA, ___,		    // se memoria nesta posicao tem um dado, usa DATA, se nao usada ee NULO ___
		JMP, JMPI, JMPIG, JMPIL, JMPIE,  JMPIM, JMPIGM, JMPILM, JMPIEM, STOP,   // desvios e parada
		ADDI, SUBI,  ADD, SUB, MULT,         // matematicos
		LDI, LDD, STD,LDX, STX, SWAP;        // movimentacao
	}

	public class CPU {
							// característica do processador: contexto da CPU ...
		private int pc; 			// ... composto de program counter,
		private Word ir; 			// instruction register,
		private int[] reg;       	// registradores da CPU

		private Word[] m;   // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre a mesma.
			
		private Aux aux = new Aux();

		public CPU(Word[] _m) {     // ref a MEMORIA e interrupt handler passada na criacao da CPU
			m = _m; 				// usa o atributo 'm' para acessar a memoria.
			reg = new int[8]; 		// aloca o espaço dos registradores
		}

		public void setContext(int _pc) {  // no futuro esta funcao vai ter que ser 
			pc = _pc;                                              // limite e pc (deve ser zero nesta versao)
		}
	
        public void showState(){
			 System.out.println("       "+ pc); 
			   System.out.print("           ");
			 for (int i=0; i<8; i++) { System.out.print("r"+i);   System.out.print(": "+reg[i]+"     "); };  
			 System.out.println("");
			 System.out.print("           ");  aux.dump(ir);
		}

		public void run() { 		// execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado
			while (true) { 			// ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
				// FETCH
					ir = m[pc]; 	// busca posicao da memoria apontada por pc, guarda em ir
					//if debug
					    showState();
				// EXECUTA INSTRUCAO NO ir
					switch (ir.opc) { // para cada opcode, sua execução

						case LDI: // Rd ← k
							reg[ir.r1] = ir.p;
							pc++;
							break;

						case STD: // [A] ← Rs
							    m[ir.p].opc = Opcode.DATA;
							    m[ir.p].p = reg[ir.r1];
							    pc++;
						break;

						case ADD: // Rd ← Rd + Rs
							reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
							pc++;
							break;

						case MULT: // Rd ← Rd * Rs
							reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
							pc++;
							break;

						case ADDI: // Rd ← Rd + k
							reg[ir.r1] = reg[ir.r1] + ir.p;
							pc++;
							break;

						case STX: // [Rd] ←Rs
							    m[reg[ir.r1]].opc = Opcode.DATA;      
							    m[reg[ir.r1]].p = reg[ir.r2];          
								pc++;
							break;

						case SUB: // Rd ← Rd - Rs
							reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
							pc++;
							break;

						case SUBI:
							reg[ir.r1] = reg[ir.r1] - ir.p;
							pc++;
							break;

						case JMP: //  PC ← k
								pc = ir.p;
						     break;
						
						case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
							if (reg[ir.r2] > 0) {
								pc = reg[ir.r1];
							} else {
								pc++;
							}
							break;

						case JMPIE: // If Rc = 0 Then PC ← Rs Else PC ← PC +1
							if (reg[ir.r2] == 0) {
								pc = reg[ir.r1];
							} else {
								pc++;
							}
							break;

						case LDX:
							reg[ir.r1] = m[reg[ir.r2]].p;
							pc++;
							break;

						
						case LDD:
							int value = ir.p;
							reg[ir.r1] = m[value].p;
							pc++;
							break;

						case STOP: // por enquanto, para execucao
							break;
					}
				
				// VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
				if (ir.opc==Opcode.STOP) {   
					break; // break sai do loop da cpu
				}
			}
		}
	}
    // ------------------ C P U - fim ------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

	
    // ------------------- V M  - constituida de CPU e MEMORIA -----------------------------------------------
    // -------------------------- atributos e construcao da VM -----------------------------------------------
	public class VM {
		public int tamMem;    
        public Word[] m;     
        public CPU cpu;    

        public VM(){   // vm deve ser configurada com endereço de tratamento de interrupcoes
	     // memória
  		 	 tamMem = 1024;
			 m = new Word[tamMem]; // m ee a memoria
			 for (int i=0; i<tamMem; i++) { m[i] = new Word(Opcode.___,-1,-1,-1); };
	  	 // cpu
			 cpu = new CPU(m);
	    }	
	}
    // ------------------- V M  - fim ------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

    // --------------------H A R D W A R E - fim -------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	// ------------------- S O F T W A R E - inicio ----------------------------------------------------------

	// ------------------- VAZIO
	

	// -------------------------------------------------------------------------------------------------------
    // -------------------  S I S T E M A --------------------------------------------------------------------

	public VM vm;

    public Sistema(){   // a VM com tratamento de interrupções
		 vm = new VM();
	}

    // -------------------  S I S T E M A - fim --------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

	
    // -------------------------------------------------------------------------------------------------------
    // ------------------- instancia e testa sistema
	public static void main(String args[]) {
		Sistema s = new Sistema();
		//s.test2();
		s.test1();
		//s.test3();
	}
    // -------------------------------------------------------------------------------------------------------
    // --------------- TUDO ABAIXO DE MAIN É AUXILIAR PARA FUNCIONAMENTO DO SISTEMA - nao faz parte 

	// -------------------------------------------- teste do sistema ,  veja classe de programas
	public void test1(){
		Aux aux = new Aux();
		Word[] p = new Programas().fibonacci;
		aux.carga(p, vm.m);
		vm.cpu.setContext(0);
		System.out.println("---------------------------------- programa carregado ");
		aux.dump(vm.m, 0, 60);
		vm.cpu.run();
		System.out.println("---------------------------------- após execucao ");
		aux.dump(vm.m, 0, 60);
	}

	public void test2(){
		Aux aux = new Aux();
		Word[] p = new Programas().progMinimo;
		aux.carga(p, vm.m);
		vm.cpu.setContext(0);
		System.out.println("---------------------------------- programa carregado ");
		aux.dump(vm.m, 0, 15);
		System.out.println("---------------------------------- após execucao ");
		vm.cpu.run();
		aux.dump(vm.m, 0, 15);
	}

	public void test3(){
		Aux aux = new Aux();
		Word[] p = new Programas().fatorial;
		aux.carga(p, vm.m);
		vm.cpu.setContext(0);
		System.out.println("---------------------------------- programa carregado ");
		aux.dump(vm.m, 0, 15);
		vm.cpu.run();
		System.out.println("---------------------------------- após execucao ");
		aux.dump(vm.m, 0, 15);
	}

	// -------------------------------------------  classes e funcoes auxiliares
    public class Aux {
		public void dump(Word w) {
			System.out.print("[ "); 
			System.out.print(w.opc); System.out.print(", ");
			System.out.print(w.r1);  System.out.print(", ");
			System.out.print(w.r2);  System.out.print(", ");
			System.out.print(w.p);  System.out.println("  ] ");
		}
		public void dump(Word[] m, int ini, int fim) {
			for (int i = ini; i < fim; i++) {
				System.out.print(i); System.out.print(":  ");  dump(m[i]);
			}
		}
		public void carga(Word[] p, Word[] m) {
			for (int i = 0; i < p.length; i++) {
				m[i].opc = p[i].opc;     m[i].r1 = p[i].r1;     m[i].r2 = p[i].r2;     m[i].p = p[i].p;
			}
		}
   }
   // -------------------------------------------  fim classes e funcoes auxiliares
	
   //  -------------------------------------------- programas aa disposicao para copiar na memoria (vide aux.carga)
   public class Programas {
	   public Word[] progMinimo = new Word[] {
		    //       OPCODE      R1  R2  P         :: VEJA AS COLUNAS VERMELHAS DA TABELA DE DEFINICAO DE OPERACOES
			//                                     :: -1 SIGNIFICA QUE O PARAMETRO NAO EXISTE PARA A OPERACAO DEFINIDA
		    new Word(Opcode.LDI, 0, -1, 999), 		
			new Word(Opcode.STD, 0, -1, 10), 
			new Word(Opcode.STD, 0, -1, 11), 
			new Word(Opcode.STD, 0, -1, 12), 
			new Word(Opcode.STD, 0, -1, 13), 
			new Word(Opcode.STD, 0, -1, 14), 
			new Word(Opcode.STOP, -1, -1, -1) };


      //Ele escreve nas posições 50 a 59 da memória os primeiros 10
	  //números da sequência de Fibonacci. Ao final, para ver a resposta, deve ser feito um dump da memória. 

	   public Word[] fibonacci10 = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
			new Word(Opcode.LDI, 1, -1, 0), 	//r1 = 0
			new Word(Opcode.STD, 1, -1, 20),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada  //r1
			new Word(Opcode.LDI, 2, -1, 1),		//r2 = 1
			new Word(Opcode.STD, 2, -1, 21),    //21 posicao de memoria onde inicia a serie de fibonacci gerada   //r2  
			new Word(Opcode.LDI, 0, -1, 22),    //   
			new Word(Opcode.LDI, 6, -1, 6),
			new Word(Opcode.LDI, 7, -1, 30),       
			new Word(Opcode.LDI, 3, -1, 0), 
			new Word(Opcode.ADD, 3, 1, -1),
			new Word(Opcode.LDI, 1, -1, 0), 
			new Word(Opcode.ADD, 1, 2, -1), 
			new Word(Opcode.ADD, 2, 3, -1),
			new Word(Opcode.STX, 0, 2, -1), 
			new Word(Opcode.ADDI, 0, -1, 1), 
			new Word(Opcode.SUB, 7, 0, -1),
			new Word(Opcode.JMPIG, 6, 7, -1), 
			new Word(Opcode.STOP, -1, -1, -1),   // POS 16
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),   // POS 20
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1),
			new Word(Opcode.DATA, -1, -1, -1)  // ate aqui - serie de fibonacci ficara armazenada
			   };   

	   public Word[] fatorial = new Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
												 // linha   coment
			new Word(Opcode.LDI, 0, -1, 6),      // 0   	r0 é valor a calcular fatorial
			new Word(Opcode.LDI, 1, -1, 1),      // 1   	r1 é 1 para multiplicar (por r0)
			new Word(Opcode.LDI, 6, -1, 1),      // 2   	r6 é 1 para ser o decremento
			new Word(Opcode.LDI, 7, -1, 8),      // 3   	r7 tem posicao de stop do programa = 8
			new Word(Opcode.JMPIE, 7, 0, 0),     // 4   	se r0=0 pula para r7(=8)
			new Word(Opcode.MULT, 1, 0, -1),     // 5   	r1 = r1 * r0
			new Word(Opcode.SUB, 0, 6, -1),      // 6   	decrementa r0 1 
			new Word(Opcode.JMP, -1, -1, 4),     // 7   	vai p posicao 4
			new Word(Opcode.STD, 1, -1, 10),     // 8   	coloca valor de r1 na posição 10
			new Word(Opcode.STOP, -1, -1, -1),    // 9   	stop
			new Word(Opcode.DATA, -1, -1, -1) };  // 10   ao final o valor do fatorial estará na posição 10 da memória                                    
    
		
		public Word[] fibonacci = new Word[] {

			new Word(Opcode.LDI, 0, -1, 5), 	// r0 = parâmetro indicador do valor máximo da sequência fibonacci
            new Word(Opcode.STD, 0, -1, 25),	// Posição 25 da memória guarda o valor de r0
            new Word(Opcode.LDD, 1, -1, 25),	// r1 recebe o valor da posição 25 da memória
            new Word(Opcode.LDI, 7, -1, 8), 	// r7 = 8 
            new Word(Opcode.JMPIG, 7, 1, -1),	// verifica o valor do registrador 1 é positivo, se sim pula para a linha 8
            new Word(Opcode.LDI, 3, -1, -1),	// caso o valor do registrador 1 seja negativo ou zero, armazena -1 no registrador 3 e cai nas duas linhas abaixo
            new Word(Opcode.STD, 3, -1, 26), 	// Posição 26 da memória guarda o valor de r3
            new Word(Opcode.STOP, -1, -1, -1),  // linha 7 - para o programa

            new Word(Opcode.LDI, 0, -1, 0),		// linha 8 - r0 = 0, o primeiro valor da sequência Fibonacci
            new Word(Opcode.LDI, 1, -1, 1),		// r1 = 1, o segundo número de Fibonacci
            new Word(Opcode.LDI, 2, -1, 27),	// Posição para a escrita do início da saída da memória
            new Word(Opcode.LDD, 3, -1, 25), 	// r3 = o valor da posição 25 da memória
            new Word(Opcode.LDI, 7, -1, 7), 	// linha 12 -   armazena no registrador 7 a posição do stop

            new Word(Opcode.STX, 2, 0, -1), 	// guarda na posição da memória guardada no r2 o valor que está no r0
            new Word(Opcode.ADDI, 2, -1, 1),	// r2 = o valor do r1++
            new Word(Opcode.SUBI, 3, -1, 1),	// r3 = o valor de r1 - 1
            new Word(Opcode.JMPIE, 7, 3, -1),	// checa se o r3 = 0, se sim pula para o valor de r7, que por conseguinte é a indicação da localização do stop
            new Word(Opcode.STX, 2, 1, -1),		// guarda na posição da memória indicada o valor
            new Word(Opcode.ADDI, 2, -1, 1), 	// r2 = o valor do r1++
            new Word(Opcode.SUBI, 3, -1, 1),	// r3 = o valor de r1 - 1
            new Word(Opcode.JMPIE, 7, 3, -1),	// checa se o r3 = 0
            new Word(Opcode.ADD, 0, 1, -1), 	// r0 = o valor do r0 + r1
            new Word(Opcode.ADD, 1, 0, -1),		// r1 = o valor de r1 + r0
            new Word(Opcode.JMP, -1, -1, 13)	// linha 23 volta para o loop

		};

		public Word[] programaC = new Word[] {
			new Word(Opcode.DATA,5,-1,43),
			new Word(Opcode.DATA,2,-1,44),
			new Word(Opcode.DATA,8,-1,45),
			new Word(Opcode.DATA,4,-1,46),
			new Word(Opcode.DATA,1,-1,47),
			new Word(Opcode.DATA,6,-1,48),
			new Word(Opcode.LDD,1,-1,43),
			new Word(Opcode.LDI,2,-1,0),
			new Word(Opcode.LDI,3,-1,0),
			new Word(Opcode.LDI,5,-1,0),
			new Word(Opcode.ADD,5,2,0),
			new Word(Opcode.SUB,5,1,0),
			new Word(Opcode.JMPIGM,5,-1,41),
			new Word(Opcode.JMPIEM,5,-1,41),
			new Word(Opcode.ADDI,2,-1,1),
			new Word(Opcode.LDI,3,-1,0),
			new Word(Opcode.LDI,5,-1,0),
			new Word(Opcode.ADD,5,3,0),
			new Word(Opcode.ADDI,5,-1,1),
			new Word(Opcode.SUB,5,1,0),
			new Word(Opcode.JMPIEM, 5,-1,9),
			new Word(Opcode.JMPIGM, 5,-1,9),
			new Word(Opcode.LDI, 4,-1,44),
			new Word(Opcode.ADD, 4,3,0),
			new Word(Opcode.LDI, 5,-1,1),
			new Word(Opcode.ADD, 5,4,0),
			new Word(Opcode.LDX, 4,4,-1),
			new Word(Opcode.LDX, 5,5,-1),
			new Word(Opcode.ADDI,3,-1,1),
			new Word(Opcode.LDI, 6,-1,0),
			new Word(Opcode.ADD, 6,5,0),
			new Word(Opcode.SUB, 6,4,1),
			new Word(Opcode.JMPILM,6,-1,34),
			new Word(Opcode.JMP, -1,-1,16),
			new Word(Opcode.SWAP,5,4,-1),
			new Word(Opcode.LDI, 6,-1,43),
			new Word(Opcode.ADD, 6,3,-1),
			new Word(Opcode.STX, 6,4,-1),
			new Word(Opcode.ADDI,6,-1,1),
			new Word(Opcode.STX, 6,5,-1),
			new Word(Opcode.JMP, -1,-1,16),
			new Word(Opcode.STOP,-1,-1,-1),
		};

		}
}

