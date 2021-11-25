# Servidor ZeroMQ - Trabalho do Grau B de Sistemas Distribuídos
Neste repositório, encontra-se uma lógica de distribuição e cadastro de informacões meteorológicas de cidades gaúchas.
- O servidor chamado `WeatherReportServer` é responsável por receber medições de temperatura enviadas por clientes que conectarem-se a ele.
- O servidor chamado `WeatherReportPublisher` é responsável por buscar periodicamente atualizações no servidor acima e publicar a média de temperatura de cada cidade que tenha dados disponíveis.
