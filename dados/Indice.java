package dados;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Indice {
    int id;
    long posicao;

    public Indice() {
    };

    public Indice(int id, long posicao) {
        this.id = id;
        this.posicao = posicao;
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPosicao() {
        return posicao;
    }

    public void setPosicao(long posicao) {
        this.posicao = posicao;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeLong(posicao);


        return baos.toByteArray();
    }

    /* LEITURA */
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        posicao = dis.readLong();

    }
    public void print() {
    System.out.printf("id: %d\n P: %d\n", this.id, this.posicao);
}
}