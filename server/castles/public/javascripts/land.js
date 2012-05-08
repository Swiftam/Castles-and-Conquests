CastlesApp.Land = Backbone.Model.extend({
    defaults: {
        'quantity': 0
    },

    buy: function() {
        var that = this;
        $.getJSON(this.url() + "/buy", {sessionId: CastlesApp.app.sessionId}, function(data) {
            that.trigger('buy:success', data);
        }).error(function(data) {
            that.trigger('buy:fail', data);
        });
    },

    url: function() { return "/land/" + this.id; }
});
CastlesApp.UserLand = Backbone.Model.extend();

CastlesApp.LandList = Backbone.Collection.extend({
    model: CastlesApp.Land,

    url: function() { return "/land/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.UserLandList = Backbone.Collection.extend({
    model: CastlesApp.UserLand,
    url: function() { return "/user/land/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.LandView = Backbone.View.extend({
    template: _.template($('#tpl-land-details').html()),

    events: {
        "click .buy": "buy"
    },

    initialize: function() {
        this.model.bind("buy:success", this.buySuccess, this);
        this.model.bind("buy:fail", this.buyFail, this);
    },

    buySuccess: function(data) {
        CastlesApp.app.user.fetch();
        CastlesApp.app.userLands.fetch();
        this.trigger("land:buy:success", data);
    },

    buyFail: function(data) {
        this.trigger("land:buy:fail", data);
    },

    buy:function () {
        var sender = this;
        if ( this.model.get('price') > CastlesApp.app.user.get('gold')) {
            CastlesApp.app.showLevelup("You can't buy this");
            return;
        }

        this.model.buy(1);
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