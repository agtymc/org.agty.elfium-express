function api(uri, method, data, action) {
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (csrfToken && csrfHeader && method !== 'GET') {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        }
    });

    let ajax = {
        dataType: "json",
        contentType: "application/json",
        type: method,
        url: uri,
        error: function(jqXHR, exception) { AgtyUtils.ajaxJsonError(jqXHR, exception); }
    };

    if (method === 'POST') {
        ajax.data = JSON.stringify(data);
    }

    $.ajax(ajax).done(function( answer ) {
        if (answer.debug) {
            AgtyUtils.echoJson(answer);
        }

        if (answer.reload) {
            location.reload();
            return true;
        }

        if (answer.location) {
            location.href = answer.location;
            return true;
        }

        if (answer.error) {
            $('[data-id="error-block"]').show().html(answer.error);
            return;
        }

        if (action) {
            action(answer);
        }

        if (answer.status === 403) {
            AgtyUtils.echo("STATUS", { body: 'Доступ запрещен' });
        }
        else if (answer.status === 401) {
            AgtyUtils.echo("STATUS", { body: 'Ошибка авторизации' });
        }
        else if (answer.status === 400) {
            AgtyUtils.echo("STATUS", { body: 'Ошибка авторизации' });
        }
    });
}
