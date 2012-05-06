var CastlesApp = {
    sessionId: null,

    router: null,

    showLevelup: function(text) {
        $("#levelup_text").text(text);
        $("#levelup").show();
    },

    closeLevelup: function() {
        $("#levelup").hide();
    },

    healthTimer: null,

    init: function(options) {
        if ( options.kontagent ) {
            this.kontagent = options.kontagent;
        }
        if ( options.sessionId ) {
            this.sessionId = options.sessionId;
        }
        CastlesApp.app = this;
        this.initGameData();
        this.initUserData();
        this.buildHud();
        this.startLevel=null;
        this.router = new CastlesApp.CastlesRouter();
        Backbone.history.start();
    },

    initGameData: function() {
        this.leaderList = new CastlesApp.LeaderList();
        this.leaderList.fetch();

        this.landList = new CastlesApp.LandList();
        this.landList.bind('reset', this.calculateIncome, this);
        this.landList.fetch();

        this.questList = new CastlesApp.QuestList();
        this.questList.fetch();

        this.unitList = new CastlesApp.UnitList();
        this.unitList.fetch();
    },

    initUserData: function() {
        this.user = new CastlesApp.User();
        this.user.bind("change:health", this.checkHealthTimer, this);

        this.userLands = new CastlesApp.UserLandList();
        this.userLands.bind('reset', this.calculateIncome, this);
        this.userLands.fetch();
    },

    // If the user's health is not the max, check again every once
    // in awhile
    checkHealthTimer: function() {
        var health = parseInt(CastlesApp.app.user.get('health'));
        var healthMax = parseInt(CastlesApp.app.user.get('healthMax'));
        if ( health < healthMax && null == CastlesApp.app.healthTimer ) {
            setInterval(function() {
                CastlesApp.app.user.fetch();
            }, 30000);
        } else if ( health >= healthMax ) {
            clearInterval(CastlesApp.app.healthTimer);
            CastlesApp.app.healthTimer = null;
        }
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
        $('#hud').html(this.hud.render(null).el);

        this.menu = new CastlesApp.MenuView({model: this.user});
        $('#menu').html(this.menu.render().el);
    }
};

CastlesApp.CastlesRouter = Backbone.Router.extend({
    routes: {
        "":"menu",
        "lands": "landListing",
        "lands/:id": "landDetails",
        "quests": "questListing",
        "quests/:id": "questDetails",
        "profile": "profile",
        "army": "unitListing",
        "army/:id": "unitDetails",
        "leaderboard": "leaderboard"
    },

    initialize:function() {
        if ( window.sizeChangeCallback ) {
            this.bind('all', function(trigger, args) {
                window.sizeChangeCallback();
            });
        }
    },

    leaderboard:function() {
        this.leaderboardView = new CastlesApp.LeaderboardListView({model:CastlesApp.app.leaderList});
        $('#content').html(this.leaderboardView.render(null).el);
    },

    unitListing:function() {
        this.unitListView = new CastlesApp.UnitListView({model:CastlesApp.app.unitList});
        $('#content').html(this.unitListView.render(null).el);
    },

    unitDetails:function(id) {
        this.unit = CastlesApp.app.unitList.get(id);
        this.unitView = new CastlesApp.UnitView({model:this.unit});
        $('#content').html(this.unitView.render(null).el);
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
        this.questListView = new CastlesApp.QuestListView({model:CastlesApp.app.questList});
        $('#content').html(this.questListView.render().el);
    },

    questDetails:function(id) {
        this.quest = CastlesApp.app.questList.get(id);
        this.questView = new CastlesApp.QuestView({model:this.quest});
        $('#content').html(this.questView.render().el);
    },

    profile:function() {
        this.profileView = new CastlesApp.ProfileView({model:CastlesApp.app.user});
        $('#content').html(this.profileView.render().el);
    }
});

CastlesApp.MenuView = Backbone.View.extend({
    tagName: 'div',

    template:_.template($('#tpl-menu').html()),

    initialize: function() {
        this.model.bind("reset", this.render, this);
    },

    render: function() {
        $(this.el).empty();
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});

CastlesApp.HudView = Backbone.View.extend({
    renderCount: 0,

    tagName: 'div',

    template:_.template($('#tpl-hud').html()),

    initialize: function() {
        this.model.bind("change:health", this.render, this);
        this.model.bind("change:healthMax", this.render, this);
        this.model.bind("change:level", this.render, this);
        this.model.bind("change:level", this.level, this);
        this.model.bind("change:xp", this.render, this);
        this.model.bind("change:income", this.render, this);
        this.model.bind("change:netWorth", this.render, this);
        this.model.bind("reset", this.render, this);
        this.model.fetch();
    },

    level: function() {
        var level = parseInt( this.model.get('level') );
        if ( null != CastlesApp.app.startLevel && CastlesApp.app.startLevel != level ) {
            CastlesApp.showLevelup('welcome to level ' + this.model.get('level'));
        }
        CastlesApp.app.startLevel = level;
    },

    render: function(eventName) {
        $(this.el).empty();
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});