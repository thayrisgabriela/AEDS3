package dados;


import java.util.Scanner;


class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Crud crud = new Crud();
        int escolha = 0;

        // MENU RECURSIVO COM OPCAO DE PARADA SE NECESSARIO
        do {
            System.out.println("\nMenu de opcoes:\n1 - Criar uma conta\n2 - Obter dados de um cliente\n" +
                    "3 - Alterar dados do cliente\n4 - Realizar uma Transferencia\n5 - Deletar\n6 - Pesquisar na lista invertida\n0 - Sair do sistema");
            System.out.print("\nQual opcao desejada:");
            escolha = sc.nextInt();
            int id = 0;
            String pesquisa= "";
            // String id2="";
            switch (escolha) {
                case 1:
                    try {
                        crud.Creat();
                    } catch (Exception e) {
                    }
                    break;

                case 2:
                    try {
                        System.out.print("Id do cliente: ");
                        id= sc.nextInt();
                        crud.Read(id);
                    } catch (Exception e) {
                    }
                    break;

                case 3:
                    try {
                        crud.Update();
                    } catch (Exception e) {
                    }
                    break;

                case 4:
                    try {
                        crud.Transferencia();
                    } catch (Exception e) {
                    }
                    break;

                case 5:
                    try {
                        crud.Delete();
                    } catch (Exception e) {
                    }
                    break;


                case 6:
                    try {
                        crud.buscaLista(pesquisa);
                    } catch (Exception e) {
                    }
                    break;

                case 0:
                    System.out.println("\nTenha um otimo dia!");
                    break;

                default:
                    System.out.println("\nPor favor, escolha uma opcao valida!");
                    break;
            }
        } while (escolha != 0);
    }
}
