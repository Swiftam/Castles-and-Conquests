CastlesApp.Quest = Backbone.Model.extend({
    url: function() {
        return "/quest/" + this.get("id");
    },

    run: function(cb) {
        var sender = this;
        $.getJSON(this.url() + "/run", {sessionId: CastlesApp.app.sessionId}, function(data) {
            sender.trigger('run:success', data);
        }).error(function(data) {
            sender.trigger('run:fail', data);
        });
    }
});

CastlesApp.QuestList = Backbone.Collection.extend({
    model: CastlesApp.Quest,
    url: function() { return "/quest/?sessionId=" + CastlesApp.app.sessionId; }
});

CastlesApp.QuestView = Backbone.View.extend({
    template: _.template($('#tpl-quest-details').html()),

    events: {
        'click .run': 'runQuest'
    },

    canRunQuest:function() {
        if ( CastlesApp.app.user.get('health') <= 0 ) {
            return false;
        }

        return true;
    },

    runQuest:function() {
        if ( this.canRunQuest() ) {
            this.model.run();
        } else {
            var questhurtView = new CastlesApp.QuestHurtDialogView({model:{
                price: 0
            }});
            $('#levelup').html(questhurtView.render().el);
            CastlesApp.app.showLevelup();
        }
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});

CastlesApp.QuestListView = Backbone.View.extend({
    tagName: 'div',

    initialize: function() {
        this.model.bind("reset", this.render, this);
    },

    render: function(eventName) {
        _.each(this.model.models, function ( quest ) {
            $(this.el).append(new CastlesApp.QuestListItemView({model:quest}).render().el)
        }, this);
        return this;
    }
});

CastlesApp.QuestListItemView = Backbone.View.extend({
    template:_.template($('#tpl-quest-list-item').html()),

    initialize: function() {
        this.model.bind("run:success", this.questSuccess, this);
        this.model.bind("run:fail", this.questFail, this);
    },

    questSuccess:function (eventData) {
        CastlesApp.app.user.set(eventData);
    },

    questFail:function (eventName) {
        var questfailView = new CastlesApp.QuestFailDialogView({model:{
            price: 0
        }});
        $('#levelup').html(questfailView.render().el);
        CastlesApp.app.showLevelup();
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
});