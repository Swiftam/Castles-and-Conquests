CastlesApp.Leader = Backbone.Model.extend({
    defaults: {
        'rank': 0,
        'name': '',
        'netWorth': 0
    }
});

CastlesApp.LeaderList = Backbone.Collection.extend({
    model: CastlesApp.Leader,
    url: function() { return "/leaderboard"; }
});

CastlesApp.LeaderboardListView = Backbone.View.extend({
    tagName: 'table',

    initialize: function() {
        this.model.bind("reset", this.render, this);
    },

    render: function(eventName) {
        $(this.el).empty();
        _.each(this.model.models, function ( leader ) {
            $(this.el).append(new CastlesApp.LeaderboardListItemView({model:leader}).render().el)
        }, this);
        return this;
    }
});

CastlesApp.LeaderboardListItemView = Backbone.View.extend({
    template:_.template($('#tpl-leaderboard-list-item').html()),

    initialize: function() {
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});