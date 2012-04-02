var CastlesApp = {
    init: function() {
        CastlesApp.app = this;
        this.initGameData();
        this.initUserData();
        this.buildHud();
        var router = new CastlesApp.CastlesRouter();
        Backbone.history.start();
    },

    initGameData: function() {
        this.landList = new CastlesApp.LandList();
        this.landList.bind('reset', this.calculateIncome, this);
        this.landList.fetch();
    },

    initUserData: function() {
        this.user = new CastlesApp.User();

        this.userLands = new CastlesApp.UserLandList();
        this.userLands.bind('reset', this.calculateIncome, this);
        this.userLands.fetch();
    },

    calculateIncome: function() {
        var sender = this;
        var income=0;
        _.each(this.userLands.models, function(land) {
            var gLand = land.get('land');
            var pLand = sender.landList.get(gLand.id);
            var quantity = land.get('quantity');
            income += gLand.income * quantity;
            if ( null != pLand ) {
                pLand.set({quantity: quantity});
            }
        });
        this.user.set({income: income});
    },

    buildHud: function() {
        this.hud = new CastlesApp.HudView({model: this.user});
        $('#hud').html(this.hud.render().el);
    }
};

CastlesApp.CastlesRouter = Backbone.Router.extend({
    routes: {
        "":"menu",
        "lands": "landListing",
        "lands/:id": "landDetails",
        "quests": "questListing",
        "quests/:id": "questDetails"
    },

    menu:function() {
        this.menuView = new CastlesApp.MenuView();
        $('#content').html(this.menuView.render().el);
    },

    landListing:function() {
        this.landListView = new CastlesApp.LandListView({model:CastlesApp.app.landList});
        $('#content').html(this.landListView.render().el);
    },

    landDetails:function(id) {
        this.land = CastlesApp.app.landList.get(id);
        this.landView = new CastlesApp.LandView({model:this.land});
        $('#content').html(this.landView.render().el);
    },

    questListing:function() {
        this.questList = new CastlesApp.QuestList();
        this.questListView = new CastlesApp.QuestListView({model:this.questList});
        this.questList.fetch();
        $('#content').html(this.questListView.render().el);
    },

    questDetails:function(id) {
        this.quest = this.questList.get(id);
        this.questView = new CastlesApp.QuestView({model:this.quest});
        $('#content').html(this.questView.render().el);
    }
});

CastlesApp.HudView = Backbone.View.extend({
    tagName: 'div',

    template:_.template($('#tpl-hud').html()),

    initialize: function() {
        this.model.bind("change:health", this.render, this);
        this.model.bind("change:healthMax", this.render, this);
        this.model.bind("change:level", this.render, this);
        this.model.bind("change:xp", this.render, this);
        this.model.bind("change:income", this.render, this);
        this.model.bind("reset", this.render, this);
        this.model.fetch();
    },

    render: function(eventName) {
        $(this.el).empty();
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});


CastlesApp.MenuView = Backbone.View.extend({
    tagName: 'ul',

    initialize: function() {
    },

    render: function(eventName) {
        return this;
    }
});