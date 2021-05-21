// Testando a validação usando jQuery
$(function(){

	// Aciona a validação ao sair do input
    $('.cpfCnpj').blur(function(){
    
        // O CPF ou CNPJ
        var cpfCnpj = $(this).val();
        
        // Testa a validação
        if (validaCpfCnpj(cpfCnpj)) {
            //alert('OK');
        } else {
            alert('CPF ou CNPJ inválido!');
        };
		
		// Testa a validação e formata se estiver OK
        if (formataCpfCnpj(cpfCnpj)) {
            $(this).val(formataCpfCnpj(cpfCnpj) );
        };
		
    });
	
	// Aciona a validação a cada tecla pressionada
    var temporizador = false;
    $('.cpfCnpj').keypress(function(){
    
        // O input que estamos utilizando
        var input = $(this);
        
        // Limpa o timeout antigo
        if (temporizador) {
            clearTimeout(temporizador);
        }
        
        // Cria um timeout novo de 500ms
        temporizador = setTimeout(function(){
            // Remove as classes de válido e inválido
            input.removeClass('valido');
            input.removeClass('invalido');
        
            // O CPF ou CNPJ
            var cpfCnpj = input.val();
            
            // Valida
            var valida = validaCpfCnpj(cpfCnpj);
            
            // Testa a validação
            if (valida) {
                input.addClass('valido');
            } else {
                input.addClass('invalido');
            }
        }, 500);
    });
});

