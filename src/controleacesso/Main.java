package controleacesso;

import java.io.IOException;
import java.util.Scanner;

/**
 * Programa principal do sistema de controle de códigos de acesso temporário.
 * Questão 6 – Integra todo o funcionamento:
 * - Carrega códigos de um arquivo .txt (UTF-8)
 * - Solicita códigos disponíveis
 * - Marca uso
 * - Gera novos quando necessário
 * - Salva o estado final
 */
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String caminhoArquivo = "data/codigos.txt";
        GerenciadorDeCodigos gerenciador = new GerenciadorDeCodigos();

        try {
            System.out.println("========== SISTEMA DE CONTROLE DE ACESSO ==========");
            System.out.println("Carregando códigos do arquivo: " + caminhoArquivo + " ...");
            gerenciador.carregarCodigos(caminhoArquivo);

            boolean continuar = true;

            while (continuar) {
                System.out.println("\n=== MENU ===");
                System.out.println("1 - Usar código de VISITANTE");
                System.out.println("2 - Usar código de TERCEIRIZADO");
                System.out.println("3 - Exibir todos os códigos");
                System.out.println("4 - Gerar novos códigos manualmente");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");
                int op = sc.nextInt();
                sc.nextLine(); // limpar buffer

                switch (op) {
                    case 1 -> {
                        CodigoAcesso visitante = gerenciador.obterCodigoDisponivel(CodigoVisitante.class);
                        if (visitante == null) {
                            System.out.println("⚠️ Nenhum código de visitante disponível. Gerando novos...");
                            gerenciador.gerarNovosCodigos(5, CodigoVisitante.class);
                            visitante = gerenciador.obterCodigoDisponivel(CodigoVisitante.class);
                        }
                        if (visitante != null) {
                            System.out.println("✅ Código de visitante atribuído: " + visitante.getCodigo());
                            visitante.usar();
                        }
                        gerenciador.salvarCodigos(caminhoArquivo);
                    }

                    case 2 -> {
                        CodigoAcesso terceirizado = gerenciador.obterCodigoDisponivel(CodigoTerceirizado.class);
                        if (terceirizado == null) {
                            System.out.println("⚠️ Nenhum código de terceirizado disponível. Gerando novos...");
                            gerenciador.gerarNovosCodigos(5, CodigoTerceirizado.class);
                            terceirizado = gerenciador.obterCodigoDisponivel(CodigoTerceirizado.class);
                        }
                        if (terceirizado != null) {
                            System.out.println("✅ Código de terceirizado atribuído: " + terceirizado.getCodigo());
                            terceirizado.usar();
                        }
                        gerenciador.salvarCodigos(caminhoArquivo);
                    }

                    case 3 -> {
                        System.out.println("\n📋 LISTA DE CÓDIGOS:");
                        System.out.println("------------------------------------");
                        for (CodigoAcesso c : gerenciadorListarTodos(gerenciador)) {
                            System.out.println(c);
                        }
                        System.out.println("------------------------------------");
                    }

                    case 4 -> {
                        System.out.println("\nQuantos novos códigos deseja gerar?");
                        int qtd = sc.nextInt();
                        System.out.println("1 - Visitante | 2 - Terceirizado");
                        int tipo = sc.nextInt();
                        if (tipo == 1)
                            gerenciador.gerarNovosCodigos(qtd, CodigoVisitante.class);
                        else
                            gerenciador.gerarNovosCodigos(qtd, CodigoTerceirizado.class);
                        gerenciador.salvarCodigos(caminhoArquivo);
                    }

                    case 0 -> {
                        continuar = false;
                        System.out.println("Encerrando o sistema...");
                        gerenciador.salvarCodigos(caminhoArquivo);
                    }

                    default -> System.out.println("❌ Opção inválida, tente novamente.");
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao manipular arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }

        sc.close();
    }

    /**
     * Método auxiliar para listar os códigos de dentro do gerenciador.
     */
    private static java.util.List<CodigoAcesso> gerenciadorListarTodos(GerenciadorDeCodigos g) {
        try {
            var campo = GerenciadorDeCodigos.class.getDeclaredField("codigos");
            campo.setAccessible(true);
            return (java.util.List<CodigoAcesso>) campo.get(g);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
