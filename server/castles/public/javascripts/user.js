CastlesApp.User = Backbone.Model.extend({
    defaults: {
        'name': 'Guy Incognito',
        'health': 0,
        'healthMax': 0,
        'level': 1,
        'gold': 0,
        'income': 0,
        'xp': 0,
        'xpGoal': 0, // XP to next level
        'netWorth': 0,

        'lands': [],

        'feature_army': false
    },
    url: function() { return "/user/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.ProfileView = Backbone.View.extend({
    template: _.template($('#tpl-profile').html()),

    initialize:function() {
        this.model.bind("change:xp", this.render, this);
        this.model.bind("change:xpGoal", this.render, this);
    },

    render: function() {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});