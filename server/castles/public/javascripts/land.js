CastlesApp.Land = Backbone.Model.extend({
    defaults: {
        'quantity': 0
    },

    buy: function(pos) {
        var that = this;
        $.getJSON(this.url() + "/buy", {sessionId: CastlesApp.app.sessionId, indexNum: pos}, function(data) {
            that.trigger('buy:success', data);
        }).error(function(data) {
            that.trigger('buy:fail', data);
        });
    },

    url: function() { return "/land/" + this.id; }
});

CastlesApp.LandList = Backbone.Collection.extend({
    model: CastlesApp.Land,

    url: function() { return "/land/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.UserLandList = Backbone.Collection.extend({
    model: CastlesApp.UserLand,
    url: function() { return "/user/land/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.UserLandListView = Backbone.View.extend({
    tagName: 'div',

    initialize: function() {
        this.model.bind('change:lands', this.render, this);
    },

    render: function(eventArgs) {
        var userLands = this.model.get('lands');
        var nLen = Math.min(CastlesApp.app.maxLands,userLands.length);
        $(this.el).empty();
        for ( var i=0; i < nLen; i++ ) {
            var land = CastlesApp.landList.get(userLands[i]);
            var userLandItem = new CastlesApp.UserLandListItemView({model:land});
            userLandItem.indexNum = i;
            $(this.el).append(userLandItem.render().el);
        }
        return this;
    }
});

CastlesApp.UserLandListItemView = Backbone.View.extend({
    template: _.template($('#tpl-userland-list-item').html()),

    events: {
        "click": "info"
    },

    info: function() {
        var upgradeLandListView = new CastlesApp.LandListView({model:CastlesApp.app.landList});
        $("#landupgrade").fadeIn();
        upgradeLandListView.parentLand = this.model.get('id');
        upgradeLandListView.indexNum = this.indexNum;
        $("#landupgrade-list").html(upgradeLandListView.render().el);
    },

    render:function () {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
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
        this.trigger("land:buy:success", data);
    },

    buyFail: function(data) {
        this.trigger("land:buy:fail", data);
    },

    buy:function () {
        var sender = this;
        if ( this.model.get('price') > CastlesApp.app.user.get('gold')) {
            var landfailView = new CastlesApp.LandfailDialogView({model:{
                price: this.model.get('price')
            }});
            $('#levelup').html(landfailView.render().el);
            CastlesApp.app.showLevelup();
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

    render: function() {
        $(this.el).empty();
        _.each(this.model.models, function ( land ) {
            if ( land.get('parent') == this.parentLand) {
                var landListItemView = new CastlesApp.LandListItemView({model:land});
                landListItemView.indexNum = this.indexNum;
                $(this.el).append(landListItemView.render().el)
            }
        }, this);
        return this;
    }
});

CastlesApp.LandListItemView = Backbone.View.extend({
    template:_.template($('#tpl-land-list-item').html()),

    events: {
        "click button": "buy"
    },

    initialize: function() {
        this.model.bind("buy:success", this.buySuccess, this);
        this.model.bind("buy:fail", this.buyFail, this);
    },

    buySuccess: function(data) {
        CastlesApp.app.user.fetch();
        this.trigger("land:buy:success", data);
        $("#landupgrade").fadeOut();
    },

    buyFail: function(data) {
        this.trigger("land:buy:fail", data);
    },

    buy:function () {
        var sender = this;
        if ( this.model.get('price') > CastlesApp.app.user.get('gold')) {
            var landfailView = new CastlesApp.LandfailDialogView({model:{
                price: this.model.get('price')
            }});
            $('#levelup').html(landfailView.render().el);
            CastlesApp.app.showLevelup();
            return;
        }

        this.model.buy(this.indexNum);
    },

    render:function () {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});