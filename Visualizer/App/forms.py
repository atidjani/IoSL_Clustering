from django import forms

class UploadDatasetForm(forms.Form):
    file = forms.FileField()
