requirejs.config({
    paths: {
        messenger: './messenger/messenger',
        bootstrap: '../lib/bootstrap/js/bootstrap',
        jquery: '../lib/jquery/jquery'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: '$'
        },
        'jquery': {
            exports: '$'
        }
    }
});

require(['messenger', 'bootstrap'], function(messenger) {
    messenger.init();
});