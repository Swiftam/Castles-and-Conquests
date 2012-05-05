CastlesApp.Registration = {
    kontagent: null,
    init:function(options) {
        if ( options.kontagent ) {
            this.kontagent = options.kontagent;
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
            name: nameField.val()
        }, function(data) {
            alert(data.snid);
            window.app = CastlesApp.init({
                sessionId: data.snid,
                kontagent: ktApi
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