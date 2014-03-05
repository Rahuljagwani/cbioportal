/**
 * PDB Table View.
 *
 * This view is designed to function in parallel with the 3D visualizer.
 *
 * options: {el: [target container],
 *           model: {geneSymbol: hugo gene symbol,
 *                   pdbColl: collection of PdbModel instances,
 *                   pdbProxy: pdb data proxy},
 *          }
 *
 * @author Selcuk Onur Sumer
 */
var PdbTableView = Backbone.View.extend({
	initialize : function (options) {
		this.options = options || {};
	},
	render: function()
	{
		var self = this;

		// compile the template using underscore
		var template = _.template($("#pdb_table_view_template").html(),
		                          {loaderImage: "images/ajax-loader.gif"});

		// load the compiled HTML into the Backbone "el"
		self.$el.html(template);

		// init pdb table
		self.pdbTable = self._initPdbTable();

		// format after rendering
		self.format();
	},
	format: function()
	{
		var self = this;

		// TODO hide view initially
		//self.$el.hide();
	},
	hideView: function()
	{
		var self = this;
		self.$el.slideUp();
	},
	showView: function()
	{
		var self = this;
		self.$el.slideDown();
	},
	/**
	 * Initializes the PDB chain table.
	 *
	 * @return {MutationPdbTable}   table instance
	 */
	_initPdbTable: function()
	{
		var self = this;

		var pdbColl = self.model.pdbColl;
		var pdbProxy = self.model.pdbProxy;

		var options = {el: self.$el.find(".pdb-chain-table")};
		var headers = ["PDB Id",
			"Chain",
			"Uniprot From",
			"Uniprot To",
			"Uniprot Positions",
			"Identity Percent",
			"Organism",
			"Summary"];
		var table = new MutationPdbTable(options, headers);

		self._generateRowData(pdbColl, pdbProxy, function(rowData) {
			// init table with the row data
			table.renderTable(rowData);
			// hide loader image
			self.$el.find(".pdb-chain-table-loader").hide();
		});

		return table;
	},
	_generateRowData: function(pdbColl, pdbProxy, callback)
	{
		var rows = [];
		var pdbIds = [];

		pdbColl.each(function(pdb) {
			pdbIds.push(pdb.pdbId);
		});

		pdbProxy.getPdbInfo(pdbIds.join(" "), function(data) {
			pdbColl.each(function(pdb) {
				pdb.chains.each(function(chain) {
					rows.push(
						[pdb.pdbId,
						chain.chainId,
						chain.mergedAlignment.uniprotFrom,
						chain.mergedAlignment.uniprotTo,
						null,
						chain.mergedAlignment.identityPerc,
						"TODO",
						data[pdb.pdbId]]
					);
				})
			});

			callback(rows);
		});
	}
});

