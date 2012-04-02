CastlesApp.Land = Backbone.Model.extend({
    defaults: {
        "quantity": 0
    },

    buy: function(quantity) {
        app.userLands.fetch();
    },

    url: function() { return "/land/" + this.id; }
});
CastlesApp.UserLand = Backbone.Model.extend();

CastlesApp.LandList = Backbone.Collection.extend({
    model: CastlesApp.Land,

    url: function() { return "/land"; }
});

CastlesApp.UserLandList = Backbone.Collection.extend({
    model: CastlesApp.UserLand,
    url: function() { return "/user/land"; }
});

CastlesApp.LandView = Backbone.View.extend({
    template: _.template($('#tpl-land-details').html()),

    events: {
        "click .buy": "buy"
    },

    buy:function () {
        var sender = this;
        $.getJSON(this.model.url() + "/buy", {}, function(data) {
            CastlesApp.userLands.fetch();
            sender.trigger('land:buy:success', data);
        }).error(function(data) {
            sender.trigger('land:buy:fail', data);
        });
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});

CastlesApp.LandListView = Backbone.View.extend({
    tagName: 'div',

    initialize: function() {
        this.model.bind("reset", this.landReset, this);
        this.model.bind('change', this.landReset, this);
    },

    landReset: function() {
        this.render();
    },

    render: function(eventName) {
        $(this.el).empty();
        _.each(this.model.models, function ( land ) {
            $(this.el).append(new CastlesApp.LandListItemView({model:land}).render().el)
        }, this);
        return this;
    }
});

CastlesApp.LandListItemView = Backbone.View.extend({
    template:_.template($('#tpl-land-list-item').html()),

    events: {
        "click .buy": "buy"
    },

    buy:function () {
        this.model.buy(1);
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});