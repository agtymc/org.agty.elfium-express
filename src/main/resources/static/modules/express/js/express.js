const expressGlobalSettings = {};

function epressInit() {
    expressPanels();
    expressGroups();
    expressButtons();
}

function formInit() {
    //changePanelType();
}

function expressButtons() {
    $('.express-page-buttons .move-button').on("click", function () {
        $('.express-page-buttons a').removeClass("active");
        settingsCopyMoveMode(false, !expressGlobalSettings.moveMode);
        dragAndDrop(expressGlobalSettings.moveMode, this);
    });

    $('.express-page-buttons .copy-button').on("click", function () {
        $('.express-page-buttons a').removeClass("active");
        settingsCopyMoveMode(!expressGlobalSettings.copyMode, false);
        dragAndDrop(expressGlobalSettings.copyMode, this);
    });
}

function settingsCopyMoveMode(copy, move) {
    expressGlobalSettings.moveMode = move;
    expressGlobalSettings.copyMode = copy;
}

function dragAndDrop(mode, object) {
    if (mode) {
        draggableInit();
        $( "#expressgroups" ).sortable('disable');
        $(object).addClass("active");
    } else {
        draggableDestroy();
        $( "#expressgroups" ).sortable('enable');
        $(object).removeClass("active");
    }
}

function expressPanels() {
    $( "#expresspanel" ).sortable({
        stop: function( event, ui ) {
            sortexpress()
        }
    }).disableSelection();
}

function expressGroups() {
    $( "#expressgroups" ).sortable({
        stop: function( event, ui ) {
            sortgroups()
        }
    }).disableSelection();
}

function sortexpress() {
    let data = [];
    let count = 0;

    $("#expresspanel a").each(function() {
        data.push({
            "id": $(this).attr('panel-id'),
            "align": count++
        });
    });

    api('/api/v1/express/panels/sort', 'POST', data);
}

function sortgroups() {
    let data = [];
    let count = 0;
    $("#expressgroups a").each(function() {
        data.push({
            "id": $(this).attr('group-id'),
            "align": count++
        });
    });

    api('/api/v1/express/groups/sort', 'POST', data);
}

function draggableInit() {
    $('.group-block, .express-block')
        .draggable({ opacity: 0.8, helper: "clone" })
        .disableSelection();

    $('.group-block, .directory-up, .express-page-path-block').droppable({
        accept: ".group-block, .express-block",
        tolerance: "pointer",
        drop: function( event, ui ) { //При отпускании объекта
            //Если идет перемещение панели в корень
            if (ui.draggable.attr('panel-id') && $(this).attr('group-id') == 1) return;

            let actionItems = [
                {
                   "object": ui.draggable.attr('panel-id') ? 'panel' : 'group',
                   "src": ui.draggable.attr('panel-id') ? ui.draggable.attr('panel-id') : ui.draggable.attr('group-id'),
                   "dst": $(this).attr('group-id'),
                }
            ];

            //AgtyUtils.echoJson(actionItems);

            api('/api/v1/express/move', 'POST', actionItems);

            ui.draggable.remove();
        }
    });
}

function draggableDestroy() {
    $('.group-block, .express-block').draggable('destroy');
    $('.group-block, .directory-up, .express-page-path-block').droppable('destroy');
}

function changePanelType() {
    let EXPRESS_TYPE = $('#idType').val();
    $('div[data-id="express-add-type-element"]').addClass('nodisplay');//.find("select, input, textarea").val('');
    if (EXPRESS_TYPE) {
        $('div[data-id-type="' + EXPRESS_TYPE + '"]').removeClass('nodisplay');
    }
}

function viewPanel(idType, idPanel) {
    if (!idType || !idPanel) return;

    api('/api/v1/express/view/' + idPanel, 'GET', null, function (answer) {
        AgtyDialog.open({
            'title': answer.title,
            'content': answer.content,
            'status': answer.status,
        });
    });
}
