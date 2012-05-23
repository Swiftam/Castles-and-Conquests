CastlesApp.LevelupDialogView = Backbone.View.extend({
    template: _.template($('#tpl-popup-levelup').html()),

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

CastlesApp.LandfailDialogView = Backbone.View.extend({
    template: _.template($('#tpl-popup-landfail').html()),

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

// When a quest fails server-side
CastlesApp.QuestFailDialogView = Backbone.View.extend({
    template: _.template($('#tpl-popup-questfail').html()),

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

// When you're too hurt to complete a quest
CastlesApp.QuestHurtDialogView = Backbone.View.extend({
    template: _.template($('#tpl-popup-questhurt').html()),

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});