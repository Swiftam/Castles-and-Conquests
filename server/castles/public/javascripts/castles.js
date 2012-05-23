var CastlesApp = {
    sessionId: null,

    maxLands: 9,

    router: null,

    levelupTimer: null,

    showLevelup: function(text) {
        //Get The Height Of Window
        var height = $(window).height();
        //Change Overlay Height
        $("#loverlay").css('height',height+'px');

        //$("#levelup_text").text(text);
        $("#levelup").show();
        $("#loverlay").fadeIn();

        this.levelupTimer = setTimeout(function() {
            $("#loverlay").click(function() {
                CastlesApp.app.closeLevelup();
            });
        }, 5000);
    },

    closeLevelup: function() {
        $("#levelup").hide();
        $("#loverlay").fadeOut();
        clearTimeout(this.levelupTimer);
        $("#loverlay").unbind('click');
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
    },

    // If the user's health is not the max, check again every once
    // in awhile
    checkHealthTimer: function() {
        var health = parseInt(CastlesApp.app.user.get('health'));
        var healthMax = parseInt(CastlesApp.app.user.get('healthMax'));
        if ( health < healthMax && null == CastlesApp.app.healthTimer ) {
            CastlesApp.app.healthTimer = setInterval(function() {
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
        "lands": "userLandListing",
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

    userLandListing:function() {
        this.userLandListView = new CastlesApp.UserLandListView({model:CastlesApp.app.user});
        $('#content').html(this.userLandListView.render(null).el);
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
            var levelupView = new CastlesApp.LevelupDialogView({model:{
                level: level
            }});
            $('#levelup').html(levelupView.render().el);
            CastlesApp.app.showLevelup();
        }
        CastlesApp.app.startLevel = level;
    },

    setupXpBar: function() {
        var xp = this.model.get('xp');
        var xpGoal = this.model.get('xpGoal');
        var xpPercent = xp / xpGoal * 100;
        $("#xpProgressIndicator").css('width', xpPercent + '%' );
    },

    render: function(eventName) {
        $(this.el).empty();
        $(this.el).html(this.template(this.model.toJSON()));
        this.setupXpBar();
        return this;
    }
});