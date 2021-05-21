function excluir() {
    if (confirm("Confirma a exclus�o?")) {
        return true;
    } else
        return false;
}

/*Funcao Pai de Mascaras*/
function Mascara(o, f) {
    v_obj = o
    v_fun = f
    setTimeout("execmascara()", 1)
}

/*Funcao que Executa os objetos*/
function execmascara() {
    v_obj.value = v_fun(v_obj.value)
}

/*Funcao que Determina as expressoess regulares dos objetos*/
function leech(v) {
    v = v.replace(/o/gi, "0")
    v = v.replace(/i/gi, "1")
    v = v.replace(/z/gi, "2")
    v = v.replace(/e/gi, "3")
    v = v.replace(/a/gi, "4")
    v = v.replace(/s/gi, "5")
    v = v.replace(/t/gi, "7")
    return v
}

/*Funcao que permite apenas numeros*/
function Integer(v) {
    return v.replace(/\D/g, "")
}

/*Funcao que padroniza telefone (99) 9999-9999*/
function Telefone(v) {
    v = v.replace(/\D/g, "");             //Remove tudo o que não é dígito
    v = v.replace(/^(\d{2})(\d)/g, "($1) $2"); //Coloca parênteses em volta dos dois primeiros dígitos
    v = v.replace(/(\d)(\d{4})$/, "$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos
    return v
}

/*Funcao que padroniza CPF*/
function Cpf(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/(\d{3})(\d)/, "$1.$2")
    v = v.replace(/(\d{3})(\d)/, "$1.$2")

    v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2")

    return v
}

/*Funcao que padroniza CEP*/
function Cep(v) {
    v = v.replace(/D/g, "")
    v = v.replace(/^(\d{5})(\d)/, "$1-$2")
    return v
}

/*Funcao que padroniza CNPJ*/
function Cnpj(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/^(\d{2})(\d)/, "$1.$2")
    v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
    v = v.replace(/\.(\d{3})(\d)/, ".$1/$2")
    v = v.replace(/(\d{4})(\d)/, "$1-$2")
    return v
}

/*Funcao que permite apenas numeros Romanos*/
function Romanos(v) {
    v = v.toUpperCase()
    v = v.replace(/[^IVXLCDM]/g, "")

    while (v.replace(/^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$/, "") != "")
        v = v.replace(/.$/, "")
    return v
}

/*Funcao que padroniza o Site*/
function Site(v) {
    v = v.replace(/^http:\/\/?/, "")
    dominio = v
    caminho = ""
    if (v.indexOf("/") > -1)
        dominio = v.split("/")[0]
    caminho = v.replace(/[^\/]*/, "")
    dominio = dominio.replace(/[^\w\.\+-:@]/g, "")
    caminho = caminho.replace(/[^\w\d\+-@:\?&=%\(\)\.]/g, "")
    caminho = caminho.replace(/([\?&])=/, "$1")
    if (caminho != "")
        dominio = dominio.replace(/\.+$/, "")
    v = "http://" + dominio + caminho
    return v
}

/*Funcao que padroniza DATA*/
function Data(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/(\d{2})(\d)/, "$1/$2")
    v = v.replace(/(\d{2})(\d)/, "$1/$2")
    return v
}

/*Funcao que padroniza DATA*/
function Hora(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/(\d{2})(\d)/, "$1:$2")
    return v
}

/*Funcao que padroniza valor monetario*/
function Valor(v) {
    v = v.replace(/\D/g, "") //Remove tudo o que nao e digito
    v = v.replace(/^([0-9]{3}\.?){3}-[0-9]{2}$/, "$1.$2");
    //v=v.replace(/(\d{3})(\d)/g,"$1,$2")
    //v=v.replace(/(\d)(\d{2})$/,"$1.$3") //Coloca ponto antes dos 2 ultimos digitos
    return v
}

/*Funcao que padroniza Area*/
function Area(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/(\d)(\d{2})$/, "$1.$2")
    return v

}

var ehPlacaValida = function (placa) {
    var er = /[a-z]{3}-?\d{4}/gim;
    er.lastIndex = 0;
    return er.test(placa);
}

$(function () {
    $("#table100").keyup(function () {
        var index = $(this).parent().index();
        var nth = "#table100 td:nth-child(" + (index + 1).toString() + ")";
        var valor = $(this).val().toUpperCase();
        $("#table100 tbody tr").show();
        $(nth).each(function () {
            if ($(this).text().toUpperCase().indexOf(valor) < 0) {
                $(this).parent().hide();
            }
        });
    });

    $("#table100 input").blur(function () {
        $(this).val("");
    });
});

(function () {
    var menuEl = document.getElementById('ml-menu'),
            mlmenu = new MLMenu(menuEl, {
                // breadcrumbsCtrl : true, // show breadcrumbs
                // initialBreadcrumb : 'all', // initial breadcrumb text
                backCtrl: false, // show back button
                // itemsDelayInterval : 60, // delay between each menu item sliding animation
                onItemClick: loadDummyData // callback: item that doesn´t have a submenu gets clicked - onItemClick([event], [inner HTML of the clicked item])
            });

    // mobile menu toggle
    var openMenuCtrl = document.querySelector('.action--open'),
            closeMenuCtrl = document.querySelector('.action--close');

    openMenuCtrl.addEventListener('click', openMenu);
    closeMenuCtrl.addEventListener('click', closeMenu);

    function openMenu() {
        classie.add(menuEl, 'menu--open');
        closeMenuCtrl.focus();
    }

    function closeMenu() {
        classie.remove(menuEl, 'menu--open');
        openMenuCtrl.focus();
    }

    // simulate grid content loading
    var gridWrapper = document.querySelector('.content');

    function loadDummyData(ev, itemName) {
        ev.preventDefault();

        closeMenu();
        gridWrapper.innerHTML = '';
        classie.add(gridWrapper, 'content--loading');
        setTimeout(function () {
            classie.remove(gridWrapper, 'content--loading');
            gridWrapper.innerHTML = '<ul class="products">' + dummyData[itemName] + '<ul>';
        }, 700);
    }
})();


$("#cep").blur(function () {
    // Remove tudo o que não é número para fazer a pesquisa
    var cep = this.value.replace(/[^0-9]/, "");

    // Validação do CEP; caso o CEP não possua 8 números, então cancela
    // a consulta
    if (cep.length != 8) {
        return false;
    }

    // A url de pesquisa consiste no endereço do webservice + o cep que
    // o usuário informou + o tipo de retorno desejado (entre "json",
    // "jsonp", "xml", "piped" ou "querty")
    var url = "https://viacep.com.br/ws/" + cep + "/json/";

    // Faz a pesquisa do CEP, tratando o retorno com try/catch para que
    // caso ocorra algum erro (o cep pode não existir, por exemplo) a
    // usabilidade não seja afetada, assim o usuário pode continuar//
    // preenchendo os campos normalmente
    $.getJSON(url, function (dadosRetorno) {
        try {
            // Preenche os campos de acordo com o retorno da pesquisa
            $("#endereco").val(dadosRetorno.logradouro);
            $("#bairro").val(dadosRetorno.bairro);
            $("#cidade").val(dadosRetorno.localidade);
            $("#uf").val(dadosRetorno.uf);
        } catch (ex) {
        }
    });
});

//PESQUISA
$('#search_input').bind('keyup click change',function(){
    search = $(this).val().toLowerCase();
    var re = new RegExp(search, 'g');

    $('#tabela-produtos tbody tr').each(function(){
        $(this).hide();
        target = $(this).find('td').text().toLowerCase();
        if(target.match(re) || target.match('^' + search)){
            $(this).show();
        }
    });
});

$(function(){
	$("#tabela input").keyup(function(){		

		var index = $(this).parent().index();
		var nth = "#tabela td:nth-child("+(index+1).toString()+")";
		var valor = $(this).val().toUpperCase();
		$("#tabela tbody tr").show();
		$(nth).each(function(){
			if($(this).text().toUpperCase().indexOf(valor) < 0){
				$(this).parent().hide();
			}
		});
	});

	$("#tabela input").blur(function(){
		$(this).val("");
	});
        if($(this).text().toUpperCase().indexOf(texto.toUpperCase()) < 0)
   $(this).css("display", "none");
});