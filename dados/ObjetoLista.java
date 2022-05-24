package dados;

public class ObjetoLista {
    public String palavra;
    public String[] ids = new String[100];
    public int quantIds;

    ObjetoLista(String s){
        String[] elements = s.split("_");
        this.palavra = elements[0];
        for(int i=1; i<elements.length; i++){
            this.ids[i-1] = elements[i];
            quantIds++;
        }
    }

    ObjetoLista(String palavra, String id){
        this.palavra = palavra;
        this.ids[0] = id;
        quantIds++;
    }

    ObjetoLista(){
        palavra = "";
        ids = null;
        quantIds = 0;
    }

    // adiciona um novo id para a palavra em questão
    public void adicionarID(String id) {
        ids[quantIds] = id;
        quantIds++;
    }

    // exclui um id da lista
    public void excluiID(String id) {
        for(int i=0; i<quantIds; i++){
            if(id.equals(ids[i])){
                int j;
                quantIds--;
                for(j=i; j<quantIds; j++){
                    ids[j] = ids[j+1];
                }
                ids[j] = null;
                return;
            }
        }
    }

    public byte[] toByteArray() {
        return toString().getBytes();
    }

    // formato registro da lista: palavra_id0_id1_id2\n
    public String toString() {
        String resp = palavra;

        for(int i=0; i < quantIds; i++){
            resp += "_" + ids[i];
        }

        resp += "\n";

        return resp;
    }
}
