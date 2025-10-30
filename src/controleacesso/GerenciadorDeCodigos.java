package controleacesso;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerencia códigos de acesso (visitantes e terceirizados),
 * com controle incremental de geração e unicidade.
 */
public class GerenciadorDeCodigos {

    private final List<CodigoAcesso> codigos = new ArrayList<>();

    private int contadorVisitante = 0;
    private int contadorTerceirizado = 0;

    // ==============================================================
    // =============== MÉTODOS PRINCIPAIS ============================
    // ==============================================================

    public void carregarCodigos(String caminho) throws IOException {
        Path arquivo = Paths.get(caminho);
        codigos.clear();

        if (!Files.exists(arquivo)) {
            System.out.println("Arquivo não encontrado. Criando novo: " + caminho);
            if (arquivo.getParent() != null) Files.createDirectories(arquivo.getParent());
            Files.createFile(arquivo);
            return;
        }

        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);

        for (String linha : linhas) {
            if (linha.trim().isEmpty()) continue;

            // Lê contadores (linhas iniciadas com #)
            if (linha.startsWith("#CONTADOR_VISITANTE=")) {
                contadorVisitante = Integer.parseInt(linha.split("=")[1]);
                continue;
            }
            if (linha.startsWith("#CONTADOR_TERCEIRIZADO=")) {
                contadorTerceirizado = Integer.parseInt(linha.split("=")[1]);
                continue;
            }

            try {
                CodigoAcesso codigo;
                if (linha.startsWith("VIS-")) {
                    codigo = new CodigoVisitante(linha);
                } else if (linha.startsWith("TER-")) {
                    codigo = new CodigoTerceirizado(linha);
                } else {
                    System.out.println("Linha ignorada (prefixo desconhecido): " + linha);
                    continue;
                }
                codigos.add(codigo);
            } catch (Exception e) {
                System.out.println("Código inválido ignorado: " + linha + " (" + e.getMessage() + ")");
            }
        }

        System.out.println("Total de códigos carregados: " + codigos.size());
        System.out.println("Contadores carregados: VIS=" + contadorVisitante + ", TER=" + contadorTerceirizado);
    }

    public CodigoAcesso obterCodigoDisponivel(Class tipo) {
        for (CodigoAcesso c : codigos) {
            if (tipo.isInstance(c) && !c.isUsado()) {
                return c;
            }
        }
        return null;
    }

    public void salvarCodigos(String caminho) throws IOException {
        Path arquivo = Paths.get(caminho);

        List<String> linhas = codigos.stream()
                .map(CodigoAcesso::getCodigo)
                .collect(Collectors.toList());

        // adiciona as linhas de controle no final
        linhas.add("#CONTADOR_VISITANTE=" + contadorVisitante);
        linhas.add("#CONTADOR_TERCEIRIZADO=" + contadorTerceirizado);

        Files.write(arquivo, linhas, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Arquivo salvo com " + codigos.size() + " códigos.");
    }

    public String gerarNovoCodigo(Class tipo) {
        String novoCodigo;

        do {
            if (tipo == CodigoVisitante.class) {
                contadorVisitante++;
                novoCodigo = gerarCodigoVisitante(contadorVisitante);
            } else if (tipo == CodigoTerceirizado.class) {
                contadorTerceirizado++;
                novoCodigo = gerarCodigoTerceirizado(contadorTerceirizado);
            } else {
                throw new IllegalArgumentException("Tipo desconhecido: " + tipo.getSimpleName());
            }
        } while (existeCodigo(novoCodigo));

        // adiciona o novo código à lista
        if (tipo == CodigoVisitante.class) {
            codigos.add(new CodigoVisitante(novoCodigo));
        } else {
            codigos.add(new CodigoTerceirizado(novoCodigo));
        }

        System.out.println("Novo código gerado: " + novoCodigo);
        return novoCodigo;
    }

    public void gerarNovosCodigos(int qtd, Class tipo) {
        for (int i = 0; i < qtd; i++) {
            gerarNovoCodigo(tipo);
        }
    }

    // ==============================================================
    // =============== MÉTODOS AUXILIARES ============================
    // ==============================================================

    private boolean existeCodigo(String codigo) {
        return codigos.stream().anyMatch(c -> c.getCodigo().equals(codigo));
    }

    private String gerarCodigoVisitante(int numero) {
        // VIS-AAA9999-X
        Random r = new Random();
        String letras = "" + (char) ('A' + r.nextInt(26))
                + (char) ('A' + r.nextInt(26))
                + (char) ('A' + r.nextInt(26));

        // base no contador (gera número de 4 dígitos)
        int numeros = 1000 + (numero % 9000);
        int soma = 0;
        for (char c : String.valueOf(numeros).toCharArray()) soma += Character.getNumericValue(c);
        int dv = soma % 10;

        return String.format("VIS-%s%d-%d", letras, numeros, dv);
    }

    private String gerarCodigoTerceirizado(int numero) {
        // TER-999-AAA-Y
        Random r = new Random();
        int numeros = 100 + (numero % 900);
        String letras = "" + (char) ('A' + r.nextInt(26))
                + (char) ('A' + r.nextInt(26))
                + (char) ('A' + r.nextInt(26));

        int soma = 0;
        for (char c : letras.toCharArray()) soma += (int) c;
        int dv = soma % 10;

        return String.format("TER-%03d-%s-%d", numeros, letras, dv);
    }
}
