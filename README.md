# ChronoMap ![](https://github.com/HenriAugusto/ChronoMap/blob/master/Icons/ChronoMap%20Icon.png)

[**Click here to read this README file in English**](https://github.com/HenriAugusto/ChronoMap/blob/master/README-en.md)


[**Clique aqui para ir à pagina de download!**](https://github.com/HenriAugusto/ChronoMap/releases)

## O que é isto?

ChronoMap é um aplicativo para criar, editar e visualizar linhas do tempo (timelines).

## Por quê?

Tudo começou como um projeto pessoal para me ajudar em meus estudos de história (principalmente história da arte). Então, como professor, percebi que era uma ferramenta muito útil nas aulas de música porque a maioria dos estudantes tinha uma compreensão básica da sequência dos eventos mas não tinha um bom entendimento de sua _"posição no tempo"_. Por exemplo: em aulas sobre história da música da música ocidental nós falamos muito de Bach e compositores posteriores. Ao somar todos estes temos provavelmente o mesmo tempo de aula que usamos para falar de Idade Média, por exemplo. Os estudantes entretanto não observam que "Idade Média" é na verdade um período de ~1000 anos (e foi assim chamado pelas pessoas da Renascença) e não conseguem portanto comparar este fato ao de que a maior parte dos compositores que estudamos atuou num período de menos de 300 anos. Então eu decidi criar este projeto para ajudar os estudantes a entender a escala temporal das coisas.

É importante notar que **a linha do tempo não é um objeto de estudo _per se_** e sim uma **ferramenta** para auxiliar estudantes nos seus estudos de história. Ela os ajuda a entender melhor _"posições no tempo"_ e os permite agrupar eventos visualmente a fim de estudar sua _posição relativa_

## Screenshot
![Looks like your browser can't display this image](https://raw.githubusercontent.com/HenriAugusto/ChronoMap/master/Readme%20Images/ChronoMap%20v0.1.0.png)

## Gif
![Looks like your browser can't display this image](https://raw.githubusercontent.com/HenriAugusto/ChronoMap/master/Readme%20Images/ChronoMap%20v0.1.0%20gif.gif)

## Funcionalidades

* **Busca de eventos:** aperte Ctrl+F e encontre o evento que procura e o aplicativo vai centralizar a visualização nele
* **Navegador web embutido**: Salve links em um evento e os acesse de dentro do próprio aplicativo. Você pode até especificar o tipo de link e, por exemplo, se for um link de áudio ele vai tocar no _background_.
* **Visualização Condicional:** Você pode adicionar condições (como "Compositores", "Pintores", etc) e selecionar a qualquer hora que eventos você quer ver. Isto é apoiado por uma sintaxe de expressões condicionais. Você pode, por exemplo, adicionar um evento para Beethoven e usar a expressão: __Compositores && (CompositoresClassico || CompositoresRomantico)__
* **Ajuda embutida:** Pressione F1 a qualquer momento para abrir uma janela que contém toda a informação que precisa. Não há necessidade de abrir PDF's ou arquivos externos.
* **Dados salvos com XML:** Toda a informação salva é armazenada em arquivos XML codificados em UTF-8 que podem ser lidas por outros aplicativos para estatísticas (e ainda é legível por humanos)
* **Timeline da história da arte:** O software ja vem com a linha do tempo da história da arte que originou o software. Não está nem perto de estar finalizada (se é que um dia estará) mas é um bom começo ;)

## Futuro

- [ ] **Visualização 3D:** Visualizar em 3D a linha do tempo. __Easter egg!__ Na verdade você ja pode ver um protótipo dessa função apertando ctrl+3 depois de carregar tua linha do tempo. Clique e arraste para mover a visualização. UseF10 and F11 para zoom (use ctrl para controlar a variação de zoom)
