$(document).ready(function () {
    setUpCheckBoxAllAndDelete();
});

function setUpCheckBoxAllAndDelete() {
    const checkBoxes = $(".checkbox")
    const checkAllBoxes = $("#check-box-all")
    checkAllBoxes.click(function () {
        checkBoxes.prop('checked', checkAllBoxes.prop('checked'));
        toggleDeleteAllSelectedBoxes();
    });
    checkBoxes.click(function () {
        checkAllBoxes.prop('checked', checkBoxes.length === checkBoxes.filter(':checked').length);
        toggleDeleteAllSelectedBoxes();
    });

    function toggleDeleteAllSelectedBoxes() {
        $(".show-btn").toggle(checkBoxes.is(':checked'))
    }
}

function showModalDeleteSelectedBoxes() {
    const selectedCount = $(".checkbox:checked").length;
    const showModal = $("#showModal");
    showModal.modal("show");
    showModal.find(".modal-title").html(`<p>Are you sure?</p>`)
    showModal.find(".modal-body").html(`<p>Are you sure you want to delete ${selectedCount} row?</p>`)
    showModal.find(".btn-danger").on("click", function () {
        showModal.modal("hide");
        deleteSelectedBoxes();
    })
}

function deleteSelectedBoxes() {
    const selectedBoxIds = $(".checkbox:checked").map(function () {
        return $(this).val();
    }).get();

    $.ajax({
        type: "POST",
        url: "/admin/user/selected/delete",
        contentType: "application/json",
        data: JSON.stringify(selectedBoxIds),
        success: function () {
            window.location.href = "/admin/users/page/1?sortField=name&sortDir=asc";
        },
        error: function (error) {
            console.log("Error deleting for users", error)
        }
    })
}
