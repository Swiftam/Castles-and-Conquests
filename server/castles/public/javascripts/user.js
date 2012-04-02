CastlesApp.User = Backbone.Model.extend({
    defaults: {
        'health': 0,
        'healthMax': 0,
        'level': 1,
        'gold': 0,
        'income': 0
    },
    url: function() { return "/user"; }
});