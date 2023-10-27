document.getElementById('file').addEventListener('change', (event) => {
    const fileInput = event.target;
    const imagePreview = document.getElementById('photo');
    if (fileInput.files && fileInput.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => {
            imagePreview.src = e.target.result;
        }
        reader.readAsDataURL(fileInput.files[0]);
    }
});

// $(document).ready(function (){
//     $("#file").change(function (){
//         showImage(this);
//     });
// });
//
// function showImage(fileInput){
//     const file = fileInput.file[0];
//     const reader = new FileReader();
//     reader.onload = function (e){
//         $("#photo").attr("src",e.target.result);
//     };
//     reader.readAsDataURL(file);
// }
