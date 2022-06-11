package dados;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.ArrayList;

class Crud {

    RandomAccessFile arq;
    RandomAccessFile arqIndice;
    RandomAccessFile listaInvertida;
    Scanner sc;
    int id = 0;

    Crud() {
        try {
            // arquivo de dados
            arq = new RandomAccessFile("dados/cliente.db", "rw");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Arquivo nao encontrado!");
        }
        try {
            // arquivo utilizado pra lista invertida
            listaInvertida= new RandomAccessFile("dados/listaInvertida.db", "rw");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Arquivo nao encontrado!");
        }
        sc = new Scanner(System.in);
        try {
            // arquivo de indices
            arqIndice = new RandomAccessFile("dados/arqIndice.db", "rw");

            byte[] by = new byte[4];
            arqIndice.read(by);
            ByteArrayInputStream bais = new ByteArrayInputStream(by);
            DataInputStream dis = new DataInputStream(bais);
            id = dis.readInt();
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        sc = new Scanner(System.in);
    }

    //creat
    public void Creat() throws Exception {
        String nome = "";
        String cpf = "";
        String cidade = "";

        byte[] by;
        byte[] by2;
        Cliente c1;

        for (int x = 0; x != 1;) {
            System.out.print("Por favor digite o nome:");
            nome = sc.nextLine();
            if (nome.length() < 100) {
                x = 1;
            } else
                System.out.println("Por favor, abrevie o nome!");
        }
        for (int x = 0; x != 1;) {
            System.out.print("Por favor digite seu cpf:");
            cpf = sc.nextLine();
            if (( cpf.length() == 11)) {
                x = 1;
            } else
                System.out.println("Por favor, digite um cpf válido!");
        }
        for (int x = 0; x != 1;) {
            System.out.print("Por favor digite sua cidade:");
            cidade = sc.nextLine();
            if (cidade.length() < 30) {
                x = 1;
            } else
                System.out.println("Por favor, abrevie o nome da cidade!");
        }
        long P = arq.length(); //pega a posição no arq de dados (no caso vamos usar para pegar a posição do id e inserir no arq de indices)
        // escreve no arq de dados

        nome = Criptografar(nome);
        c1 = new Cliente(id, nome, cpf, cidade, P);
        c1.print();
        by = c1.toByteArray();
        arq.seek(arq.length());
        arq.writeChar(' ');
        arq.writeInt(by.length);
        arq.write(by);

        //escreve no arq de indices mantendo a coerencia com o de dados
        Indice Indice = new Indice(id, P);
        by2 = Indice.toByteArray();
        arqIndice.seek(arqIndice.length());
        arqIndice.write(by2);

        // quando cria um registro ele é salvo no arq da lista
        addArqLista(Integer.toString(id), nome);
		addArqLista(Integer.toString(id), cidade);
        id++;
    }


    public String Criptografar (String nome){
        String auxil = new String();
        for ( int i  = 0; i < nome.length(); i++){
                auxil = auxil+ (char)(nome.charAt(i) + 3);
        }
        return auxil;
        }



    // read
    public Cliente Read(int idCliente) throws Exception{
        byte[] by;
        Cliente c1;

        char lapide;
        int tam = arq.readInt();
        int idRead = -1;

        try{
            int i = 4;
            //Percorre o arq até achar o id, printa caso seja achado e tbm confere com a lapide se ele está ativo
            while (idCliente != idRead && i < arq.length()) { 
                arq.seek(i);

                lapide = arq.readChar();
                i += 2;
                arq.seek(i);
                tam = arq.readInt();
                i += 4;
                
                if (lapide == ' ') {
                    arq.seek(i);
                    idRead = arq.readInt();
                }
                i += tam;
            }
            if (idCliente == idRead) {
                // pega a posição do id encontrado na busca binária 
                long P = BuscaBin(idCliente);
                if(idCliente == 0)
                arq.seek(P + 10);//aqui forçamos a achar a posição certa do id sempre 
                if(idCliente != 0)
                arq.seek(P + 6); //aqui forçamos a achar a posição certa do id sempre 
                by = new byte[tam];
                arq.read(by);
                c1 = new Cliente(P);
                c1.fromByteArray3(by);
                c1.print();

            } else {
                System.out.println("Arquivo não identificado");
            }
            
        } catch (Exception e) {
            System.out.println("Arquivo vazio");
        }
        return null;
    }

    public long BuscaBinLogica(ArrayList<Indice> listaDeIndices, int id){
        int inicio = 0;
        int fim = listaDeIndices.size() - 1; // Posição inicial e final do array
        
        /* Enquanto a posição do inicio for menor ou igual a posição do fim,
        procura o valor de x dentro do array. */
        while (inicio <= fim)
        { 
            int meio = inicio + (fim - inicio) / 2; //Encontra o meio do vetor.

        /* Se o valor que está no meio do array é igual ao valor procurado, 
        imprime que encontrou o valor e para de buscar. */
            if (listaDeIndices.get(meio).getId() == id ) {
                // encontrou o numero
                return meio; 
            }
        /* Este if serve para diminuir o tamanho do array pela métade. */
        /* Se o valor que está no meio do array for menor que o valor de x, 
        então o inicio do array será igual a posição do meio + 1. */
            if (listaDeIndices.get(meio).getId() < id) {
                inicio = meio + 1; 
            }
                /* Se o valor que está no meio do array for maior que o valor de x, 
                então o fim do vetor será igual a posição do meio - 1. */
            else
                fim = meio - 1; 
        } 
    
        /* Caso não encontre o valor buscado dentro do array,
        imprime que não encontrou o valor buscado. */
        return -1; 
    }

    //Usamos esse metodo para criar um listArray e assim chamar nossa busca binária em cima delel
    public long BuscaBin(int id) throws Exception {
        long posicaoCliente = 0;
        byte[] by;
    
        // transforma os indices numa lista de array
        ArrayList<Indice> listaDeIndices = new ArrayList<Indice>();
        Indice indiceAux;
        try {
            // le a lista e add o id novo na lista
            arqIndice.seek(0);
            while (arqIndice.getFilePointer() < arqIndice.length()) {
                indiceAux = new Indice();
                by = new byte[12];
                arqIndice.read(by);

                indiceAux.fromByteArray(by);
                listaDeIndices.add(indiceAux);
            }
        } catch (Exception e) {
        }

        BuscaBinLogica(listaDeIndices, id);

       //Retorna posicao do id se presente na busca binaria
        posicaoCliente = listaDeIndices.get(id).getPosicao();
        return posicaoCliente;
    }

    // update
    public void Update() throws Exception{
        int idCliente;
        byte[] by;
        Cliente c1;

    System.out.print("Id do Cliente: "); 
        idCliente = sc.nextInt();

        char lapide2;
        int tam2 = 0;
        int idUpdate = -1;

        try{
            int i = 4;

         while (idCliente != idUpdate && i < arq.length()) { //procura o arq que vai ser atualizado
                arq.seek(i);

                lapide2 = arq.readChar();
            
            i += 2;
            arq.seek(i);
            tam2 = arq.readInt();
            i += 4;
            
            if (lapide2 == ' ') {
                arq.seek(i);
                idUpdate = arq.readInt();
            }
            i += tam2;
        }

         if (idCliente == idUpdate) { //Pede as novas informações do cliente que será atualizado 

            String nome2 = "";
            String cpf2 = "";
            String cidade2 = "";
            int transferencias = -1;
            float saldo = 0F;

            for(int x = 0; x != 1;){
                System.out.print("Por favor digite o nome:"); 
                sc.nextLine();
                nome2 = sc.nextLine();
                if(nome2.length() < 100){
                    x = 1;
                }
                else System.out.println("Por favor, abrevie o nome!");
            }
            for(int x = 0; x != 1;){
                System.out.print("Por favor digite seu cpf:"); cpf2 = sc.nextLine();
                if((cpf2.length() == 11)){
                    x = 1;
                }else System.out.println("Por favor, digite um cpf válido!");
            }
            for(int x = 0; x != 1;){
                System.out.print("Por favor digite sua cidade:"); cidade2 = sc.nextLine();
                if(cidade2.length() < 30){
                    x = 1;
                }else System.out.println("Por favor, abrevie o nome da cidade!");
            }
            for(int x = 0; x != 1;){
                System.out.print("Por favor digite as tranferencias realizadas:"); transferencias = sc.nextInt();
                x=1;
            }
            for(int x = 0; x != 1;){
                System.out.print("Por favor digite seu saldo:"); saldo = sc.nextInt();
                x=1;
            }
            long P = BuscaBin(idCliente);
            c1 = new Cliente(idCliente, nome2, cpf2, cidade2, transferencias, saldo, P);
            by = c1.toByteArray();

             //Se o novo resgistro for menor ou igual o atual, escreve por cima do atual
            if (by.length <= tam2){
                arq.seek(i - tam2);
                arq.write(by);
            }
             //Caso seja maior, a lapide é "desativada" e o novo resgistro é escrito no final do arq
            else{
                arq.seek(P);
                arq.writeChar('*');
                arq.seek(arq.length());
                arq.writeChar(' ');
                arq.writeInt(by.length);
                arq.write(by);
            }
        } else {
            System.out.println("Arquivo não identificado");
        }

    }catch (Exception e) {
        System.out.println("Arquivo vazio");
    }
}

    // transferencia
    public void Transferencia() throws Exception {
        byte[] by;
        Cliente c1;
        Cliente c2;

        int idCliente1;
        int idCliente2;
        int qntTransferida = 0;

        System.out.print("Id do cliente 1: "); // O debitado
        idCliente1 = sc.nextInt();
        System.out.print("Id do cliente 2: "); // O creditado
        idCliente2 = sc.nextInt();
        System.out.print("Quanto você quer transferir: ");
        qntTransferida = sc.nextInt();
        char lapide3;
        int tam3 = 0;
        int idDetectado3 = -1;

        try {
            int i = 4;

            while (idCliente1 != idDetectado3 && i < arq.length()) {
                arq.seek(i);

                lapide3 = arq.readChar();
                i += 2;
                arq.seek(i);
                tam3 = arq.readInt();
                i += 4;

                if (lapide3 == ' ') {
                    arq.seek(i);
                    idDetectado3 = arq.readInt();
                }
                i += tam3;
            }

            if (idCliente1 == idDetectado3) {
                long P = BuscaBin(idCliente1);
                c1 = new Cliente();
                by = c1.toByteArray();
                if(idCliente1 == 0)//aqui forçamos a achar a posição certa do id sempre 
                arq.seek(P + 10);
                if(idCliente1 != 0)
                arq.seek(P + 6); //aqui forçamos a achar a posição certa do id sempre 
                by = new byte[tam3];
                arq.read(by);
                c1.fromByteArray(by);
                c1.saldoConta -= qntTransferida;
                c1.transferenciasRealizadas++;

                c1.print(); 
                arq.seek(P + 6); //aqui forçamos a achar a posição certa do id sempre
                arq.write(c1.toByteArray());
            } else {
                System.out.println("Arquivo não identificado");
            }

            while (idCliente2 != idDetectado3 && i < arq.length()) {
                arq.seek(i);

                lapide3 = arq.readChar();
                i += 2;
                arq.seek(i);
                tam3 = arq.readInt();
                i += 4;

                if (lapide3 == ' ') {
                    arq.seek(i);
                    idDetectado3 = arq.readInt();
                }
                i += tam3;
            }

            if (idCliente2 == idDetectado3) {
                long P = BuscaBin(idCliente2);
                if(idCliente2 == 0)//aqui forçamos a achar a posição certa do id sempre 
                arq.seek(P + 10);
                if(idCliente2 != 0)
                arq.seek(P + 6); //aqui forçamos a achar a posição certa do id sempre 
                by = new byte[tam3];
                arq.read(by);
                c2 = new Cliente();
                c2.fromByteArray(by);
                c2.saldoConta += qntTransferida;
                c2.transferenciasRealizadas++;
                c2.print();
                arq.seek(P + 6);
                arq.write(c2.toByteArray());
            } else {
                System.out.println("Arquivo não identificado");
            }
        } catch (Exception e) {
            System.out.println("Arquivo vazio");
        }
    }

    // delete
    public void Delete() throws Exception {
        int idCliente;
        byte[] by;
        Cliente c1;

        System.out.print("Id do cliente: ");
        idCliente = sc.nextInt();
		deleteIDLista(Integer.toString(idCliente));

        char lapide4;
        int tam4 = 0;
        int idDelete = -1;

        try {
            int i = 4;
            while (idCliente != idDelete && i < arq.length()) { // procura o arq que vai ser atualizado
                arq.seek(i);

                lapide4 = arq.readChar();
                i += 2;
                arq.seek(i);
                tam4 = arq.readInt();
                i += 4;

                if (lapide4 == ' ') {
                    arq.seek(i);
                    idDelete = arq.readInt();
                }
                i += tam4;
            }

            // Caso a lapide identificada seja igual a "*", simbolo usado para identificar
            // se ela está "desativada", siginifca que o registro foi deletetado
            if (idCliente == idDelete) {
                long P = BuscaBin(idCliente);
                arq.seek(P);
                arq.writeChar('*');
                System.out.println("\nArquivo deletado:");

                arq.seek(i - tam4);
                by = new byte[tam4];
                arq.read(by);
                c1 = new Cliente();
                c1.fromByteArray3(by);
                c1.print();
            } else {
                System.out.println("Arquivo não identificado");
            }
        } catch (Exception e) {
            System.out.println("Arquivo vazio");
        }
    }    
    

    public void addArqLista(String id, String string) throws IOException {
		string = string.toLowerCase();
        //o metodo retira sinais graficos, passa para lower case e descarta stop word da String
        //retorna um array com os temros validos limpos
        ArrayList<String> stopWords = new ArrayList<>();
        stopWords.add("do");
        stopWords.add("da");
        stopWords.add("e");
        stopWords.add("dos");
        stopWords.add("de");
		String[] palavras = string.split(" ");

		String auxiliar;
		String tudo = "";
        
        // Passa por todas as palavras dos indices a serem adicionados
		for(int i=0; i<palavras.length; i++) {
			ObjetoLista ObjetoLista = null;
			
				listaInvertida.seek(0);
                // Percorre o arq até achar o que está procurando
				while(listaInvertida.getFilePointer() < listaInvertida.length()){ 
                    // tira os espaços
					auxiliar = (listaInvertida.readLine()).replaceAll(" ", "");// 

					// Esse if serve para palavras repetidas, em vez de add novamente, apenas acrescenta o id que tem a mesma palavra
					if ( palavras[i].equals( auxiliar.split("_")[0] ) ) {	
						ObjetoLista = new ObjetoLista(auxiliar);
						ObjetoLista.adicionarID(id);
						tudo += ObjetoLista.toString();
					} else {
						tudo += auxiliar + "\n";
					}
				}
			
			if (ObjetoLista == null) {
				//Se a palavra não existir no arquivo ainda, criar um novo registro da lista com a palvra
				ObjetoLista = new ObjetoLista(palavras[i], id);
			
                byte[] by;
                by = ObjetoLista.toByteArray();
                listaInvertida.seek(listaInvertida.length());
                listaInvertida.write(by, 0, by.length);


            }else{
                byte[] by;
                by = tudo.getBytes();
                listaInvertida.seek(0);
                listaInvertida.writeInt(by.length);
                listaInvertida.write(by);
            }
		}
	}

	// Realiza uma busca na lista baseada em nome e/ou estado e retorna as contas correspondentes
    public ObjetoLista buscaLista(String busca) throws NumberFormatException, Exception {
        
        System.out.print("\nDigite o nome ou cidade que deseja buscar: ");
        busca = sc.nextLine();
        
        busca = busca.toLowerCase();
		String[] palavras = busca.split(" ");
		String registro;

        // Passa por todas as palavras dos indices a serem adicionados
		for(int i=0; i<palavras.length; i++) { 
			ObjetoLista ObjetoLista = null;
				listaInvertida.seek(0);
                // Percorre o arq até achar o que está procurando
				while(listaInvertida.getFilePointer() < listaInvertida.length()){  
					registro = (listaInvertida.readLine()).replaceAll(" ", "");
                    // a palavra procurada existe no arquivo
                    if (palavras[i].equals(registro.split("_")[0])) {	
						ObjetoLista = new ObjetoLista(registro);
						for(int j=0; j<ObjetoLista.quantIds; j++){
                            // printa o cliennte procurado se ele existir
							Cliente cliente = Read(Integer.parseInt(ObjetoLista.ids[j]));
                            System.out.println("Cliente # " + cliente.idCliente + " - "+ cliente.nomePessoa +"  / Cidade: " + cliente.cidade + "\nSaldo: " + cliente.saldoConta + "\nTransferencias realizadas: " + cliente.transferenciasRealizadas);
						}
						
					}
				}
			if (ObjetoLista == null) {
            System.out.println("Essa palavra não está presente no arquivo");
			}
		}
        return null;
    }

    public void deleteIDLista(String id) throws IOException {
		String registro;
		ObjetoLista ObjetoLista;
		String tudo = "";

		listaInvertida.seek(0);

        // Percorre o arq até achar o que está procurando
        while( listaInvertida.getFilePointer() < listaInvertida.length()){ 
			registro = listaInvertida.readLine();
			ObjetoLista = new ObjetoLista(registro);
			ObjetoLista.excluiID(id);
			tudo += ObjetoLista.toString();
		}

		listaInvertida.seek(0);
		Write(listaInvertida, tudo.getBytes(), tudo.getBytes().length, 0);
		if(listaInvertida.getFilePointer() < listaInvertida.length()){
			do {
				listaInvertida.write(' ');
			}while(listaInvertida.getFilePointer() < listaInvertida.length());
		}
    }
    
    void Write(RandomAccessFile targetFile, byte[] value, int size, int position) throws IOException 
	{
		targetFile.seek(position);
		targetFile.write(value, 0, size);
	}
}
