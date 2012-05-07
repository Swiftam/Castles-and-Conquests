CastlesApp.Registration = {
    kontagent: null,
    snid: null,

    init:function(options) {
        if ( options.kontagent ) {
            this.kontagent = options.kontagent;
        }
        if ( options.snid ) {
            this.snid = options.snid;
        }
        this.registrationView = new CastlesApp.Registration.RegistrationView();
        $('#content').html(this.registrationView.render().el);
        $('#name').focus();
    }
}

CastlesApp.Registration.RegistrationView = Backbone.View.extend({
    template:_.template($('#tpl-registration').html()),

    events: {
        "submit #registerForm": "register"
    },

    register:function(form) {
        var nameField = $('input[name=name]');
        $.getJSON("/register", {
            name: nameField.val(),
            snid: CastlesApp.Registration.snid
        }, function(data) {
            window.app = CastlesApp.init({
                sessionId: data.snid,
                kontagent: CastlesApp.Registration.kontagent,
            });
        }).error(function(data) {
            alert('register failed');
            console.log(data);
        });
        return false;
    },

    render:function (eventName) {
        $(this.el).html(this.template());
        return this;
    }
});