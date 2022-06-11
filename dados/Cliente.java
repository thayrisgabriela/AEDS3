package dados;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.io.RandomAccessFile;


public class Cliente {
    //Informações pessoais do cliente
  protected int idCliente;
  protected String nomePessoa;
  protected String cpf;
  protected String cidade;
  protected int transferenciasRealizadas;
  protected float saldoConta;
  protected long P;
  protected String teste;

  /* CONSTRUTOR */
  public Cliente (int idCliente, String nomePessoa, String cpf2, String cidade, int transferenciasRealizadas, float saldoConta, long P) {
    this.idCliente = idCliente;
    this.nomePessoa = nomePessoa;
    this.cpf = cpf2;
    this.cidade = cidade;
    this.transferenciasRealizadas = transferenciasRealizadas;
    this.saldoConta = saldoConta;
    this.P = P;
  }

  public Cliente (int idCliente, String nomePessoa, String cpf, String cidade, long P) throws IOException {
    this.idCliente = setId();
    this.nomePessoa = nomePessoa;
    this.cpf = cpf;
    this.cidade = cidade;
    this.transferenciasRealizadas = 0;
    this.saldoConta = 100;
    this.P=P;
  }
 public Cliente (int transferenciasRealizadas, float saldoConta) throws IOException {
    this.idCliente = setId();
    this.transferenciasRealizadas = 0;
    this.saldoConta = 100;
  }
  public Cliente (long P) throws IOException {
    this.idCliente = setId();
    this.transferenciasRealizadas = 0;
    this.saldoConta = 100;
    this.P = P;
  }

  public Cliente (int idCliente,String teste) throws IOException {
    this.idCliente = setId2();
    this.transferenciasRealizadas = 0;
    this.saldoConta = 100;
    this.teste = teste;
  }
  
    /* CONSTRUTOR NULO */
  public Cliente() {
    this.idCliente = -1;
    this.nomePessoa = "";
    this.cpf = "";
    this.cidade = "";
    this.transferenciasRealizadas = -1;
    this.saldoConta = 0F;
    this.P = -1;
    this.teste = "";
  }

  
  public int setId() throws IOException{
    RandomAccessFile arq = new RandomAccessFile("dados/cliente.db", "rw");
    arq.seek(0);
    int id;
    
        //captar o id que sera utilizado pelo cliente
        byte[] ba = new byte[4];
        arq.read(ba);
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();

    

    //escrever o novo id do cabecalho
    byte[] ba2;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(id + 1);
    ba2 = baos.toByteArray();
    arq.seek(0);
    arq.write(ba2);
    
    arq.close();

    return id;
}

public int setId2() throws IOException{
  RandomAccessFile arq2 = new RandomAccessFile("dados/arquivoInd.db", "rw");
  arq2.seek(0);
  int id;
  
      //captar o id que sera utilizado pelo cliente
      byte[] ba = new byte[4];
      arq2.read(ba);
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);
      id = dis.readInt();

  

  //escrever o novo id do cabecalho
  byte[] ba2;
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  DataOutputStream dos = new DataOutputStream(baos);
  dos.writeInt(id + 1);
  ba2 = baos.toByteArray();
  arq2.seek(0);
  arq2.write(ba2);
  
  arq2.close();

  return id;
}

      /* PRINT */
  public String toString() {
    DecimalFormat df = new DecimalFormat("#,##0.00");

    return "\nID: " + this.idCliente + 
            "\nNome: " + this.nomePessoa + 
            "\nCPF: " + this.cpf + 
            "\nCidade: " + this.cidade + 
            "\nTransferencias: " + this.transferenciasRealizadas + 
            "\nSaldo: R$ " + df.format(this.saldoConta)+ 
            "\nEndereço:" + this.P;

  }

    /* ESCRITA */
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(idCliente);
    dos.writeUTF(nomePessoa);
    dos.writeUTF(cpf);
    dos.writeUTF(cidade);
    dos.writeInt(transferenciasRealizadas);
    dos.writeFloat(saldoConta);
    return baos.toByteArray();
  }


    /* LEITURA */
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    idCliente = dis.readInt();
    nomePessoa = dis.readUTF();
    cpf = dis.readUTF();
    cidade = dis.readUTF();
    transferenciasRealizadas = dis.readInt();
    saldoConta = dis.readFloat();

}
public String Descriptografar (String nomePessoa){
  String auxil = new String();
  for ( int i  = 0; i < nomePessoa.length(); i++){
          auxil = auxil+ (char)(nomePessoa.charAt(i) - 3);
  }
  return auxil;
  }
public void fromByteArray3(byte[] ba) throws IOException {
  ByteArrayInputStream bais = new ByteArrayInputStream(ba);
  DataInputStream dis = new DataInputStream(bais);
  idCliente = dis.readInt();
  nomePessoa = dis.readUTF();
  nomePessoa = Descriptografar(nomePessoa);
  cpf = dis.readUTF();
  cidade = dis.readUTF();
  transferenciasRealizadas = dis.readInt();
  saldoConta = dis.readFloat();
}

public byte[] toByteArray2() throws IOException {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  DataOutputStream dos = new DataOutputStream(baos);
  dos.writeInt(idCliente);
  dos.writeLong(P);
  dos.writeBytes(teste);
  return baos.toByteArray();
}


  /* LEITURA */
public void fromByteArray2(byte[] ba) throws IOException {
  ByteArrayInputStream bais = new ByteArrayInputStream(ba);
  DataInputStream dis = new DataInputStream(bais);
  idCliente = dis.readInt();
  P = dis.readLong();
  teste = dis.readUTF();


}

public void print() {
  System.out.printf("id: %d\n nome: %s\n cpf: %s\n cidade: %s\n transferenciasRealizadas: %d\n saldoConta: %f\n P: %d\n", this.idCliente, this.nomePessoa, this.cpf, this.cidade, this.transferenciasRealizadas, this.saldoConta, this.P);
}
public Cliente print2() {
  System.out.printf("id: %d\n nome: %s\n cpf: %s\n cidade: %s\n transferenciasRealizadas: %d\n saldoConta: %f\n P: %d\n", this.idCliente, this.nomePessoa, this.cpf, this.cidade, this.transferenciasRealizadas, this.saldoConta, this.P);
  return null;
}
}

