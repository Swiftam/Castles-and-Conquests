CastlesApp.Unit = Backbone.Model.extend();

CastlesApp.UnitList = Backbone.Collection.extend({
    model: CastlesApp.Unit,

    url: function() { return "/unit"; }
});

CastlesApp.UnitView = Backbone.View.extend({
    template: _.template($('#tpl-unit-details').html()),

    events: {
        "click .buy": "buy"
    },

    initialize: function() {
        this.model.bind("buy:success", this.buySuccess, this);
        this.model.bind("buy:fail", this.buyFail, this);
    },

    buySuccess: function(data) {
        this.trigger("unit:buy:success", data);
    },

    buyFail: function(data) {
        this.trigger("unit:buy:fail", data);
    },

    buy:function () {
        if ( this.model.get('price') > CastlesApp.app.user.get('gold')) {
            CastlesApp.app.showLevelup("You can't buy this.");
            return;
        }

        this.model.buy(1);
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});

CastlesApp.UnitListView = Backbone.View.extend({
    tagName: 'div',

    initialize: function() {
        this.model.bind("reset", this.render, this);
        this.model.bind('change', this.render, this);
    },

    render: function(eventName) {
        $(this.el).empty();
        _.each(this.model.models, function ( land ) {
            $(this.el).append(new CastlesApp.UnitListItemView({model:land}).render().el)
        }, this);
        return this;
    }
});

CastlesApp.UnitListItemView = Backbone.View.extend({
    template:_.template($('#tpl-unit-list-item').html()),

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});