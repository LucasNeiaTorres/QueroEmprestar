function verificaCpfCnpj(valor) {

    // Garante que o valor é uma string
    valor = valor.toString();
    
    // Remove caracteres inválidos do valor
    valor = valor.replace(/[^0-9]/g, '');

    // Verifica CPF
    if (valor.length === 11) {
        return 'CPF';
    } 
    
    // Verifica CNPJ
    else if (valor.length === 14) {
        return 'CNPJ';
    } 
    
    // Não retorna nada
    else {
        return false;
    }
    
} // verificaCpfCnpj

/*
 calcDigitosPosicoes
 
 Multiplica dígitos vezes posições
 
 @param string digitos Os digitos desejados
 @param string posicoes A posição que vai iniciar a regressão
 @param string somaDigitos A soma das multiplicações entre posições e dígitos
 @return string Os dígitos enviados concatenados com o último dígito
*/
function calcDigitosPosicoes(digitos, posicoes = 10, somaDigitos = 0) {

    // Garante que o valor é uma string
    digitos = digitos.toString();

    // Faz a soma dos dígitos com a posição
    // Ex. para 10 posições:
    //   0    2    5    4    6    2    8    8   4
    // x10   x9   x8   x7   x6   x5   x4   x3  x2
    //   0 + 18 + 40 + 28 + 36 + 10 + 32 + 24 + 8 = 196
    for ( var i = 0; i < digitos.length; i++  ) {
        // Preenche a soma com o dígito vezes a posição
        somaDigitos = somaDigitos + ( digitos[i] * posicoes );

        // Subtrai 1 da posição
        posicoes--;

        // Parte específica para CNPJ
        // Ex.: 5-4-3-2-9-8-7-6-5-4-3-2
        if ( posicoes < 2 ) {
            // Retorno a posição para 9
            posicoes = 9;
        }
    }

    // Captura o resto da divisão entre somaDigitos dividido por 11
    // Ex.: 196 % 11 = 9
    somaDigitos = somaDigitos % 11;

    // Verifica se somaDigitos é menor que 2
    if (somaDigitos < 2) {
        // somaDigitos agora será zero
        somaDigitos = 0;
    } else {
        // Se for maior que 2, o resultado é 11 menos somaDigitos
        // Ex.: 11 - 9 = 2
        // Nosso dígito procurado é 2
        somaDigitos = 11 - somaDigitos;
    }

    // Concatena mais um dígito aos primeiro nove dígitos
    // Ex.: 025462884 + 2 = 0254628842
    var cpf = digitos + somaDigitos;

    // Retorna
    return cpf;
    
} // calcDigitosPosicoes

/*
 Valida CPF
 
 Valida se for CPF
 
 @param  string cpf O CPF com ou sem pontos e traço
 @return bool True para CPF correto - False para CPF incorreto
*/
function validaCpf(valor) {

    // Garante que o valor é uma string
    valor = valor.toString();
    
    // Remove caracteres inválidos do valor
    valor = valor.replace(/[^0-9]/g, '');


    // Captura os 9 primeiros dígitos do CPF
    // Ex.: 02546288423 = 025462884
    var digitos = valor.substr(0, 9);

    // Faz o cálculo dos 9 primeiros dígitos do CPF para obter o primeiro dígito
    var novoCpf = calcDigitosPosicoes(digitos);

    // Faz o cálculo dos 10 dígitos do CPF para obter o último dígito
    var novoCpf = calcDigitosPosicoes(novoCpf, 11);

    // Verifica se o novo CPF gerado é idêntico ao CPF enviado
    if (novoCpf === valor) {
        // CPF válido
        return true;
    } else {
        // CPF inválido
        return false;
    }
    
} // validaCpf

/*
 validaCnpj
 
 Valida se for um CNPJ
 
 @param string cnpj
 @return bool true para CNPJ correto
*/
function validaCnpj (valor) {

    // Garante que o valor é uma string
    valor = valor.toString();
    
    // Remove caracteres inválidos do valor
    valor = valor.replace(/[^0-9]/g, '');

    
    // O valor original
    var cnpjOriginal = valor;

    // Captura os primeiros 12 números do CNPJ
    var primeirosNumerosCnpj = valor.substr(0, 12);

    // Faz o primeiro cálculo
    var primeiroCalculo = calcDigitosPosicoes(primeirosNumerosCnpj, 5);

    // O segundo cálculo é a mesma coisa do primeiro, porém, começa na posição 6
    var segundoCalculo = calcDigitosPosicoes(primeiroCalculo, 6);

    // Concatena o segundo dígito ao CNPJ
    var cnpj = segundoCalculo;

    // Verifica se o CNPJ gerado é idêntico ao enviado
    if (cnpj === cnpjOriginal) {
        return true;
    }
    
    // Retorna falso por padrão
    return false;
    
} // validaCnpj

/*
 validaCpfCnpj
 
 Valida o CPF ou CNPJ
 
 @access public
 @return bool true para válido, false para inválido
*/
function validaCpfCnpj(valor) {

    // Verifica se é CPF ou CNPJ
    var valida = verificaCpfCnpj(valor);

    // Garante que o valor é uma string
    valor = valor.toString();
    
    // Remove caracteres inválidos do valor
    valor = valor.replace(/[^0-9]/g, '');


    // Valida CPF
    if (valida === 'CPF') {
        // Retorna true para cpf válido
        return validaCpf(valor);
    } 
    
    // Valida CNPJ
    else if (valida === 'CNPJ') {
        // Retorna true para CNPJ válido
        return validaCnpj(valor);
    } 
    
    // Não retorna nada
    else {
        return false;
    }
    
} // validaCpfCnpj

/*
 formataCpfCnpj
 
 Formata um CPF ou CNPJ

 @access public
 @return string CPF ou CNPJ formatado
*/
function formataCpfCnpj(valor) {

    // O valor formatado
    var formatado = false;
    
    // Verifica se é CPF ou CNPJ
    var valida = verificaCpfCnpj(valor);

    // Garante que o valor é uma string
    valor = valor.toString();
    
    // Remove caracteres inválidos do valor
    valor = valor.replace(/[^0-9]/g, '');


    // Valida CPF
    if (valida === 'CPF') {
    
        // Verifica se o CPF é válido
        if (validaCpf(valor)) {
        
            // Formata o CPF ###.###.###-##
            formatado  = valor.substr( 0, 3 ) + '.';
            formatado += valor.substr( 3, 3 ) + '.';
            formatado += valor.substr( 6, 3 ) + '-';
            formatado += valor.substr( 9, 2 ) + '';
        }
    }
    
    // Valida CNPJ
    else if (valida === 'CNPJ') {
    
        // Verifica se o CNPJ é válido
        if (validaCnpj(valor)) {
        
            // Formata o CNPJ ##.###.###/####-##
            formatado  = valor.substr( 0,  2 ) + '.';
            formatado += valor.substr( 2,  3 ) + '.';
            formatado += valor.substr( 5,  3 ) + '/';
            formatado += valor.substr( 8,  4 ) + '-';
            formatado += valor.substr( 12, 2 ) + '';
        }
    } 

    // Retorna o valor 
    return formatado;
    
} // formataCpfCnpj