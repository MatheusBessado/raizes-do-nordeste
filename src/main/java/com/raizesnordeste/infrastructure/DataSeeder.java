package com.raizesnordeste.infrastructure;

import com.raizesnordeste.domain.enums.PerfilUsuario;
import com.raizesnordeste.infrastructure.persistence.entity.*;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UsuarioRepository usuarios,
                           UnidadeRepository unidades,
                           ProdutoRepository produtos,
                           EstoqueRepository estoques,
                           FidelidadeRepository fidelidade,
                           PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() > 0) {
                log.info("Seed já executado. Pulando.");
                return;
            }

            log.info("Executando seed inicial...");

            // Usuários
            UsuarioEntity admin = usuarios.save(UsuarioEntity.builder()
                    .nome("Administrador")
                    .email("admin@raizesnordeste.com")
                    .senhaHash(encoder.encode("Admin@123"))
                    .perfil(PerfilUsuario.ADMIN).ativo(true).consentimentoFidelidade(false).build());

            UsuarioEntity cliente = usuarios.save(UsuarioEntity.builder()
                    .nome("Maria Silva")
                    .email("maria@email.com")
                    .senhaHash(encoder.encode("Senha@123"))
                    .perfil(PerfilUsuario.CLIENTE).ativo(true).consentimentoFidelidade(true).build());

            usuarios.save(UsuarioEntity.builder()
                    .nome("João Cozinha")
                    .email("cozinha@raizesnordeste.com")
                    .senhaHash(encoder.encode("Cozinha@123"))
                    .perfil(PerfilUsuario.COZINHA).ativo(true).consentimentoFidelidade(false).build());

            // Fidelidade para a cliente
            fidelidade.save(FidelidadeEntity.builder()
                    .usuario(cliente).saldoPontos(0).build());

            // Unidades
            UnidadeEntity fortaleza = unidades.save(UnidadeEntity.builder()
                    .nome("Raízes - Fortaleza Centro")
                    .endereco("Rua Major Facundo, 500")
                    .cidade("Fortaleza").estado("CE").ativa(true).build());

            UnidadeEntity recife = unidades.save(UnidadeEntity.builder()
                    .nome("Raízes - Recife Boa Viagem")
                    .endereco("Av. Boa Viagem, 1200")
                    .cidade("Recife").estado("PE").ativa(true).build());

            // Produtos
            ProdutoEntity tapioca = produtos.save(ProdutoEntity.builder()
                    .nome("Tapioca Nordestina").descricao("Tapioca com queijo coalho e carne de sol")
                    .preco(new BigDecimal("28.90")).categoria("Lanches").disponivel(true).build());

            ProdutoEntity baiao = produtos.save(ProdutoEntity.builder()
                    .nome("Baião de Dois").descricao("Arroz com feijão de corda, queijo e charque")
                    .preco(new BigDecimal("45.90")).categoria("Pratos").disponivel(true).build());

            ProdutoEntity caldo = produtos.save(ProdutoEntity.builder()
                    .nome("Caldo de Cana").descricao("Caldo de cana gelado 500ml")
                    .preco(new BigDecimal("12.00")).categoria("Bebidas").disponivel(true).build());

            // Estoque por unidade
            estoques.save(EstoqueEntity.builder().unidade(fortaleza).produto(tapioca).quantidade(50).build());
            estoques.save(EstoqueEntity.builder().unidade(fortaleza).produto(baiao).quantidade(30).build());
            estoques.save(EstoqueEntity.builder().unidade(fortaleza).produto(caldo).quantidade(100).build());
            estoques.save(EstoqueEntity.builder().unidade(recife).produto(tapioca).quantidade(40).build());
            estoques.save(EstoqueEntity.builder().unidade(recife).produto(baiao).quantidade(0).build()); // sem estoque
            estoques.save(EstoqueEntity.builder().unidade(recife).produto(caldo).quantidade(80).build());

            log.info("Seed concluído! Usuários, unidades, produtos e estoques criados.");
            log.info("Admin: admin@raizesnordeste.com / Admin@123");
            log.info("Cliente: maria@email.com / Senha@123");
            log.info("Cozinha: cozinha@raizesnordeste.com / Cozinha@123");
        };
    }
}
