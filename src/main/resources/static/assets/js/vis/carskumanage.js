var InitiateSearchableDataTable = function () {
    return {
        init: function () {
            var oTable = $('#searchable').dataTable({
                "sDom": "Tflt<'row DTTTFooter'<'col-sm-6'i><'col-sm-6'p>>",
                // "sDom": 'rt<"#pBottom"p>',
                "searching":false,
                "bLengthChange": false,
                "bAutoWidth":true,
                "aaSorting": [[1, 'asc']],
                "aLengthMenu": [
                   [10, 15, 20],
                   [10, 15, 20]
                ],
                "iDisplayLength": 10,
                "oTableTools": {
                    "aButtons": [
				        //"copy",
				        //"print",
				        // {
				        //     "sExtends": "collection",
				        //     "sButtonText": "Save <i class=\"fa fa-angle-down\"></i>",
				        //     "aButtons": ["csv", "xls", "pdf"]
				        // }
                        ]
                    // "sSwfPath": "assets/swf/copy_csv_xls_pdf.swf"
                },
                "language": {
                    "search": "",
                    "sLengthMenu": "每页显示 _MENU_ 条记录",
                    "zeroRecords": "没有找到记录",
                    "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
                    "infoEmpty": "无记录",
                    "infoFiltered": "(从 _MAX_ 条记录过滤)",
                    "oPaginate": {
                        "sPrevious": "上一页",
                        "sNext": "下一页"
                    }
                }
                // "scrollX": "true",
                // "scrollY":"560"
                
            });

            $("tfoot input").keyup(function () {
                /* Filter on the column (the index) of this element */
                oTable.fnFilter(this.value, $("thead input").index(this));
            });

        }
    }
}();
