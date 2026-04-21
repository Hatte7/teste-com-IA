import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FantasyCharacterGenerator {
    private static final Random RNG = new Random();

    private static final String[] NOMES = {
        "Aldren", "Brisia", "Cael", "Darian", "Elyra", "Fenn", "Gorim", "Helia",
        "Ivar", "Jora", "Kaelith", "Liora", "Marek", "Nyra", "Orin", "Pyria"
    };

    private static final String[] TITULOS = {
        "do Crepúsculo", "das Cinzas", "da Bruma", "de Ferro", "da Aurora",
        "do Abismo", "da Tempestade", "dos Sussurros"
    };

    private static final String[] FEITICOS = {
        "Lança Arcana", "Prisma de Raio", "Névoa Astral", "Selo de Ruptura", "Orbe de Gelo"
    };

    private static final String[] INVOCACOES = {
        "Totem da Matilha", "Guardião de Éter", "Eco Ancestral", "Chama Espiritual", "Vulto da Floresta"
    };

    private static final String[] TECNICAS = {
        "Passo Fantasma", "Corte de Vanguarda", "Postura do Meteoro", "Ritmo de Guerra", "Muralha de Aura"
    };

    public static void main(String[] args) {
        int quantidade = 5;
        if (args.length > 0) {
            try {
                quantidade = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {
                System.out.println("Argumento inválido. Usando quantidade padrão = 5.");
            }
        }

        System.out.println("=== GERADOR FANTÁSTICO MEDIEVAL ===");
        for (int i = 1; i <= quantidade; i++) {
            CharacterProfile personagem = gerarPersonagem();
            System.out.println("\n--- Personagem " + i + " ---");
            System.out.println(personagem.formatar());
        }
    }

    private static CharacterProfile gerarPersonagem() {
        Rank rank = weightedPick(Arrays.asList(
            new WeightedItem<>(new Rank("S", 2, 90, 110, 10), 2),
            new WeightedItem<>(new Rank("A", 6, 80, 95, 8), 6),
            new WeightedItem<>(new Rank("B", 12, 70, 82, 6), 12),
            new WeightedItem<>(new Rank("C", 20, 58, 72, 4), 20),
            new WeightedItem<>(new Rank("D", 24, 50, 62, 2), 24),
            new WeightedItem<>(new Rank("E", 22, 42, 54, 1), 22),
            new WeightedItem<>(new Rank("F", 14, 35, 46, 0), 14)
        ));

        Archetype classe = weightedPick(Arrays.asList(
            new WeightedItem<>(new Archetype("Cavaleiro Rúnico", new int[]{3, 1, 2, 2, 0, 1}), 16),
            new WeightedItem<>(new Archetype("Bardo das Lâminas", new int[]{1, 3, 2, 2, 1, 1}), 13),
            new WeightedItem<>(new Archetype("Guardião Espiritual", new int[]{2, 1, 1, 2, 4, 1}), 12),
            new WeightedItem<>(new Archetype("Arcanista de Guerra", new int[]{0, 1, 1, 1, 1, 5}), 11),
            new WeightedItem<>(new Archetype("Assassino Umbral", new int[]{1, 1, 4, 1, 1, 1}), 14),
            new WeightedItem<>(new Archetype("Monge de Aura", new int[]{2, 1, 3, 4, 1, 0}), 10),
            new WeightedItem<>(new Archetype("Mercenário Errante", new int[]{2, 2, 2, 2, 1, 1}), 24)
        ));

        int[] atributos = gerarAtributos(rank, classe);
        Weapon arma = gerarArma(rank, atributos);

        int magia = atributos[5] * 2 + roll(0, 8);
        int espirito = atributos[4] * 2 + roll(0, 8);
        int aura = atributos[3] * 2 + roll(0, 8);

        String foco;
        if (magia >= espirito && magia >= aura) foco = "Magia (Feitiços)";
        else if (espirito >= magia && espirito >= aura) foco = "Espírito (Invocações)";
        else foco = "Aura (Técnicas)";

        String habilidadeM = FEITICOS[RNG.nextInt(FEITICOS.length)];
        String habilidadeE = INVOCACOES[RNG.nextInt(INVOCACOES.length)];
        String habilidadeA = TECNICAS[RNG.nextInt(TECNICAS.length)];

        String nome = NOMES[RNG.nextInt(NOMES.length)] + " " + TITULOS[RNG.nextInt(TITULOS.length)];
        int moral = roll(40, 100);
        int fama = Math.min(100, roll(10, 50) + rank.nivelPoder);
        int resistencia = (atributos[0] + atributos[2] + atributos[3]) / 3 + rank.nivelPoder / 2;

        return new CharacterProfile(nome, rank.nome, classe.nome, atributos, arma, magia, espirito, aura,
            foco, habilidadeM, habilidadeE, habilidadeA, moral, fama, resistencia);
    }

    private static int[] gerarAtributos(Rank rank, Archetype classe) {
        int[] attrs = {5, 5, 5, 5, 5, 5}; // Força, Carisma, Destreza, Presença, Empatia, Mana
        int pontos = roll(rank.pontosMin, rank.pontosMax);

        List<WeightedItem<Integer>> pesos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            pesos.add(new WeightedItem<>(i, Math.max(1, classe.bias[i] + 1)));
        }

        while (pontos > 0) {
            int idx = weightedPick(pesos);
            if (attrs[idx] < 25) {
                attrs[idx]++;
                pontos--;
            } else {
                pontos--;
            }
        }
        return attrs;
    }

    private static Weapon gerarArma(Rank rank, int[] attrs) {
        WeaponBase base = weightedPick(Arrays.asList(
            new WeightedItem<>(new WeaponBase("Espadão de Pedra-Estelar", 16, 5, 6), 13),
            new WeightedItem<>(new WeaponBase("Adaga de Véu", 9, 3, 18), 18),
            new WeightedItem<>(new WeaponBase("Arco de Teixo Negro", 12, 18, 9), 16),
            new WeightedItem<>(new WeaponBase("Martelo Litúrgico", 18, 4, 5), 10),
            new WeightedItem<>(new WeaponBase("Lança Dracônica", 14, 14, 8), 14),
            new WeightedItem<>(new WeaponBase("Cetro de Runas", 11, 11, 10), 9),
            new WeightedItem<>(new WeaponBase("Manoplas Rituais", 10, 2, 16), 20)
        ));

        int dano = base.danoBase + rank.bonusArma + attrs[0] / 3 + attrs[5] / 6;
        int alcance = base.alcanceBase + attrs[3] / 4;
        int velocidade = base.velocidadeBase + attrs[2] / 3;
        int chanceCritica = Math.min(60, 5 + attrs[2] + rank.nivelPoder / 2);
        int chanceEfeito = Math.min(70, 8 + attrs[4] / 2 + attrs[5] / 3);

        String efeito = weightedPick(Arrays.asList(
            new WeightedItem<>("Sangramento (2 turnos)", 16),
            new WeightedItem<>("Quebra de Armadura (-2 resistência)", 12),
            new WeightedItem<>("Roubo de Essência (+vida ao acertar)", 8),
            new WeightedItem<>("Silêncio Arcano (bloqueia feitiço)", 10),
            new WeightedItem<>("Atordoamento (chance de perder turno)", 15),
            new WeightedItem<>("Marca Espiritual (+dano em invocados)", 9),
            new WeightedItem<>("Impulso de Aura (+velocidade no próximo turno)", 14),
            new WeightedItem<>("Sem efeito especial", 16)
        ));

        return new Weapon(base.nome, dano, alcance, velocidade, chanceCritica, chanceEfeito, efeito);
    }

    private static int roll(int min, int max) {
        return min + RNG.nextInt(max - min + 1);
    }

    private static <T> T weightedPick(List<WeightedItem<T>> itens) {
        int total = 0;
        for (WeightedItem<T> i : itens) total += i.peso;
        int pick = RNG.nextInt(total);
        int acum = 0;
        for (WeightedItem<T> i : itens) {
            acum += i.peso;
            if (pick < acum) return i.valor;
        }
        return itens.get(itens.size() - 1).valor;
    }

    private static class WeightedItem<T> {
        T valor;
        int peso;
        WeightedItem(T valor, int peso) {
            this.valor = valor;
            this.peso = peso;
        }
    }

    private static class Rank {
        String nome;
        int nivelPoder;
        int pontosMin;
        int pontosMax;
        int bonusArma;
        Rank(String nome, int nivelPoder, int pontosMin, int pontosMax, int bonusArma) {
            this.nome = nome;
            this.nivelPoder = nivelPoder;
            this.pontosMin = pontosMin;
            this.pontosMax = pontosMax;
            this.bonusArma = bonusArma;
        }
    }

    private static class Archetype {
        String nome;
        int[] bias;
        Archetype(String nome, int[] bias) {
            this.nome = nome;
            this.bias = bias;
        }
    }

    private static class WeaponBase {
        String nome;
        int danoBase;
        int alcanceBase;
        int velocidadeBase;
        WeaponBase(String nome, int danoBase, int alcanceBase, int velocidadeBase) {
            this.nome = nome;
            this.danoBase = danoBase;
            this.alcanceBase = alcanceBase;
            this.velocidadeBase = velocidadeBase;
        }
    }

    private static class Weapon {
        String nome;
        int dano;
        int alcance;
        int velocidade;
        int critico;
        int efeitoChance;
        String efeito;
        Weapon(String nome, int dano, int alcance, int velocidade, int critico, int efeitoChance, String efeito) {
            this.nome = nome;
            this.dano = dano;
            this.alcance = alcance;
            this.velocidade = velocidade;
            this.critico = critico;
            this.efeitoChance = efeitoChance;
            this.efeito = efeito;
        }
    }

    private static class CharacterProfile {
        String nome;
        String rank;
        String classe;
        int[] atributos;
        Weapon arma;
        int magia;
        int espirito;
        int aura;
        String foco;
        String feitico;
        String invocacao;
        String tecnica;
        int moral;
        int fama;
        int resistencia;

        CharacterProfile(String nome, String rank, String classe, int[] atributos, Weapon arma,
                         int magia, int espirito, int aura, String foco, String feitico,
                         String invocacao, String tecnica, int moral, int fama, int resistencia) {
            this.nome = nome;
            this.rank = rank;
            this.classe = classe;
            this.atributos = atributos;
            this.arma = arma;
            this.magia = magia;
            this.espirito = espirito;
            this.aura = aura;
            this.foco = foco;
            this.feitico = feitico;
            this.invocacao = invocacao;
            this.tecnica = tecnica;
            this.moral = moral;
            this.fama = fama;
            this.resistencia = resistencia;
        }

        String formatar() {
            return "Nome: " + nome + "\n" +
                "Rank: " + rank + " | Classe: " + classe + "\n" +
                "Atributos -> Força: " + atributos[0] + ", Carisma: " + atributos[1] + ", Destreza: " + atributos[2] +
                ", Presença: " + atributos[3] + ", Empatia: " + atributos[4] + ", Mana: " + atributos[5] + "\n" +
                "Manifestação -> " + foco + " | Magia: " + magia + ", Espírito: " + espirito + ", Aura: " + aura + "\n" +
                "Kit de combate -> Feitiço: " + feitico + " | Invocação: " + invocacao + " | Técnica: " + tecnica + "\n" +
                "Arma: " + arma.nome + " (Dano: " + arma.dano + ", Alcance: " + arma.alcance +
                ", Velocidade: " + arma.velocidade + ", Crítico: " + arma.critico + "%, Proc. Efeito: " + arma.efeitoChance + "%)\n" +
                "Efeito da arma: " + arma.efeito + "\n" +
                "Status extras -> Moral: " + moral + ", Fama: " + fama + ", Resistência: " + resistencia;
        }
    }
}
