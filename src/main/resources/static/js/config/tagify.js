window.addEventListener('load', function () {

    const getWhiteList = function () {
        return new Promise(function (resolve) {
            $.get('/api/tags', function (response) {
                resolve(response);
            });
        });
    };


    getWhiteList().then(function (data) {

        const tagify = new Tagify(document.querySelector('#tagInput'), {
            pattern: /^[가-힣a-z0-9_]{1,16}$/,
            whitelist: JSON.parse('' + data),
            maxTags: 3,
            dropdown: {enabled: 1}
        });


        const tagRequest = function (method, name) {
            $.ajax({
                method: method,
                url: "/api/tags/" + name,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('X-CSRF-TOKEN', $('#tagify-csrf').text());
                }
            });
        };

        const onAdd = function (e) {
            tagRequest("put", e.detail.data.value);
        };

        const onRemove = function (e) {
            tagRequest("delete", e.detail.data.value);
        };


        tagify.on("add", onAdd);
        tagify.on("remove", onRemove);

    });

});
